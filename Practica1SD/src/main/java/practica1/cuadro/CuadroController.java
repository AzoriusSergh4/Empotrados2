package practica1.cuadro;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import practica1.GaleriaController;
import practica1.autor.Autor;
import practica1.autor.AutorRepository;
import practica1.cliente.Cliente;
import practica1.cliente.ClienteRepository;

@Controller
@RequestMapping("/cuadro")
public class CuadroController extends GaleriaController{

    @Autowired
    private CuadroRepository cuadroRepository;
    
    @Autowired
    private AutorRepository autorRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;

    @RequestMapping("/mostrarCuadros")
    public String mostrarCuadros(Model model) {
    	cargaGaleria(model);    	
        return "cuadros";
    }

    @RequestMapping("/addCuadro")
    public String addCuadro(Model model) {
    	List<Autor> autores = autorRepository.findAll();
    	List<Cliente> clientes = clienteRepository.findAll();
    	model.addAttribute("autores", autores);
    	model.addAttribute("clientes", clientes);
    	
        return "nuevoCuadro";
    }

    @PostMapping("/")
    public String addCuadro(Model model, @RequestParam Map<String, String> mappedCuadro) {
        Cuadro cuadro = crearCuadroDesdeMap(mappedCuadro);
        this.cuadroRepository.save(cuadro);
        cargaGaleria(model);

        return "cuadros";
    }

    @RequestMapping("/editarCuadro/{id}")
    public String editarCuadro(Model model, @PathVariable long id) {
        Optional<Cuadro> opcional = this.cuadroRepository.findById(id);
        opcional.ifPresent(cuadro -> model.addAttribute("cuadro", cuadro));

        List<Autor> autores = autorRepository.findAll();
    	List<Cliente> clientes = clienteRepository.findAll();
    	model.addAttribute("autores", autores);
    	model.addAttribute("clientes", clientes);

        return "editarCuadro";
    }
    
    @PostMapping("/{id}")
    public String editarCuadro(Model model, @PathVariable long id, @RequestParam Map<String, String> mappedCuadro) {
        Cuadro cuadro = this.crearCuadroDesdeMap(mappedCuadro);

        Optional<Cuadro> opcional = this.cuadroRepository.findById(id);
        if(opcional.isPresent()){
            Cuadro cuadroAnterior = opcional.get();
            cuadroAnterior.actualizarCuadro(cuadro);
            this.cuadroRepository.save(cuadroAnterior);
        }
        cargaGaleria(model);

        return "cuadros";
    }

    @GetMapping("/{id}")
    public String consultaCuadro(Model model, @PathVariable long id) {
        Optional<Cuadro> opcional = this.cuadroRepository.findById(id);
        opcional.ifPresent(cuadro -> model.addAttribute("cuadro", cuadro));

        return "infoCuadro";
    }
    
    @GetMapping("/buscarPorTituloODescripcion")
    public String buscarCuadroPorTituloODescripcion(Model model, @RequestParam String tituloDescripcion) {
        if (tituloDescripcion == null || tituloDescripcion.equals("")) {
            cargaGaleria(model);
        } else {
            cargaGaleria(model);
            model.addAttribute("cuadros", cuadroRepository.findDistinctCuadroByTituloContainsIgnoreCaseOrDescripcionContainsIgnoreCase(tituloDescripcion, tituloDescripcion));
        }

        return "cuadros";
    }
    
    @GetMapping("/buscarPorAutor")
    public String buscarCuadroPorAutor(Model model, @RequestParam(required = false) Autor autor) {
        if (autor == null) {
            cargaGaleria(model);
        } else {
            cargaGaleria(model);
            model.addAttribute("cuadros", cuadroRepository.findByAutor(autor));
        }

        return "cuadros";
    }

    @GetMapping("/ordenar")
    public String buscarOrdenado(Model model, @RequestParam String sort) {
        cargaGaleria(model);
        model.addAttribute("cuadros", cuadroRepository.findAll(Sort.by(sort)));
        return "cuadros";
    }
    
    private Cuadro crearCuadroDesdeMap(Map<String, String> mappedCuadro) {
        Cuadro cuadro = new Cuadro();
        SimpleDateFormat fecha = new SimpleDateFormat("yyyy-MM-dd");
        cuadro.setTitulo(mappedCuadro.get("titulo"));
        cuadro.setDescripcion(mappedCuadro.get("descripcion"));
        cuadro.setAltura(Double.parseDouble(mappedCuadro.get("altura")));
        cuadro.setAnchura(Double.parseDouble(mappedCuadro.get("anchura")));
        cuadro.setAnyoFinalizacion(Integer.parseInt(mappedCuadro.get("anyoFinalizacion")));
    	cuadro.setPrecio(Integer.parseInt(mappedCuadro.get("precio")));
    	if(mappedCuadro.get("fechaVenta").equals("")){
    	    cuadro.setFechaVenta(null);
        } else {
            try {
                cuadro.setFechaVenta(new Date(fecha.parse(mappedCuadro.get("fechaVenta")).getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    	Cliente comprador = (mappedCuadro.get("comprador").equals("null") ? null : clienteRepository.findByNif(mappedCuadro.get("comprador")));
    	cuadro.setComprador(comprador);
    	Autor autor = autorRepository.findByNif(mappedCuadro.get("autor"));
    	cuadro.setAutor(autor);

        return cuadro;
    }
}
