package practica1.autor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import practica1.GaleriaController;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/autor")
public class AutorController extends GaleriaController{
    @Autowired
    private AutorRepository autorRepository;

    @RequestMapping("/addAutor")
    public String addAutor(Model model) {
        return "nuevoAutor";
    }

    @RequestMapping("/editarAutor/{id}")
    public String editarAutor(Model model, @PathVariable long id) {
        Optional<Autor> opcional = this.autorRepository.findById(id);
        if(opcional.isPresent()){
            model.addAttribute("autor", opcional.get());
        }

        return "editarAutor";
    }

    @PostMapping("/")
    public String addAutor(Model model, @RequestParam Map<String, String> mappedAutor) {
        Autor autor = this.crearAutorDesdeMap(mappedAutor);

        this.autorRepository.save(autor);
        cargaGaleria(model);
        return "galeria";
    }

    @GetMapping("/{id}")
    public String editarAutor(Model model, @PathVariable long id,  @RequestParam Map<String, String> mappedAutor) {
        Autor autor = this.crearAutorDesdeMap(mappedAutor);

        Optional<Autor> opcional = this.autorRepository.findById(id);
        if(opcional.isPresent()){
            Autor autorAnterior = opcional.get();
            autorAnterior.actualizarAutor(autor);
            this.autorRepository.save(autorAnterior);
        }
        cargaGaleria(model);
        return "galeria";
    }

    private Autor crearAutorDesdeMap(Map<String, String> mappedAutor) {
        Autor autor = new Autor();

        autor.setNombre(mappedAutor.get("nombre"));
        autor.setApellidos(mappedAutor.get("apellidos"));
        autor.setNif(mappedAutor.get("nif"));
        autor.setAnyoNacimiento(Integer.parseInt(mappedAutor.get("anyoNacimiento")));
        autor.setPaisNacimiento(mappedAutor.get("paisNacimiento"));
        autor.setEmail(mappedAutor.get("email"));
        autor.setTelefono(mappedAutor.get("telefono"));
        autor.setDireccionPostal(mappedAutor.get("direccionPostal"));

        return autor;
    }
}