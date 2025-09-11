package school.sptech.prova_ac1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        List<Usuario> userList = usuarioRepository.findAll();

        if (userList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {

        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent() ||
                usuarioRepository.findByCpf(usuario.getCpf()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Usuario salvo = usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        Usuario userFound = usuarioRepository.findById(id).orElse(null);

        if (userFound == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(userFound);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        if (usuarioRepository.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        usuarioRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/filtro-data")
    public ResponseEntity<List<Usuario>> buscarPorDataNascimento(@RequestParam LocalDate nascimento) {
        List<Usuario> userList = usuarioRepository.findByDataNascimentoAfter(nascimento);

        if (userList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Integer id,
            @RequestBody Usuario usuario
    ) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!existente.getEmail().equals(usuario.getEmail()) &&
                usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        if (!existente.getCpf().equals(usuario.getCpf()) &&
                usuarioRepository.findByCpf(usuario.getCpf()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        existente.setNome(usuario.getNome());
        existente.setEmail(usuario.getEmail());
        existente.setCpf(usuario.getCpf());
        existente.setSenha(usuario.getSenha());
        existente.setDataNascimento(usuario.getDataNascimento());

        Usuario atualizado = usuarioRepository.save(existente);
        return ResponseEntity.status(HttpStatus.OK).body(atualizado);
    }
}
