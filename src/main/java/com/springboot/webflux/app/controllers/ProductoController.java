package com.springboot.webflux.app.controllers;

import java.time.Duration;
import java.util.Date;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;

import com.springboot.webflux.app.models.dao.ProductoDAO;
import com.springboot.webflux.app.models.documents.Categoria;
import com.springboot.webflux.app.models.documents.Producto;
import com.springboot.webflux.app.models.services.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class ProductoController {

	@Autowired
	private ProductoService service;

	private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

	@ModelAttribute("categorias")
	private Flux<Categoria> categorias(){
		return service.findAllCategoria();
		
	}
	
	@GetMapping({ "/listar", "/" })
	public String listar(Model model) {
		Flux<Producto> productos = service.findAllByNombreUpperCase();

		productos.subscribe(prod -> log.info(prod.getNombre())); 

		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "Listado de productos");

		return "listar";
	}

	@GetMapping("/form")
	public Mono<String> crear(Model model) {
		model.addAttribute("producto", new Producto());
		model.addAttribute("titulo", "Formulario de producto");
		model.addAttribute("boton", "Crear");
		
		return Mono.just("form");
	}
	
	@GetMapping("/form-v2/{id}")
	public Mono<String> editarV2(@PathVariable String id, Model model){
		
		return service.findById(id).doOnNext( p -> {
			log.info("Producto id" + p.getNombre());
			
			model.addAttribute("boton","Editar");
			model.addAttribute("titulo","Editar producto");
			model.addAttribute("producto", p);
		}).defaultIfEmpty( new Producto() )
		  .flatMap(p ->{
			  if(p.getId() == null) {
				  return Mono.error(new InterruptedException("No existe el producto"));
			  }
			  return Mono.just(p);
		  })	
		  //Retornar la vista
		  .then(Mono.just("form"))
		  .onErrorResume( ex -> Mono.just("redirect:/listar?error=no+existe+el+producto"));
		
	}
	
	@GetMapping("/eliminar/{id}")
	public Mono<String> eliminar(@PathVariable String id){
		return service.findById(id).flatMap( p -> {
			return service.delete(p);
		}).then(Mono.just("redirect:/listar?success=producto+eliminado+con+exito"));
	}
	
	
	
	@GetMapping("/form/{id}")
	public Mono<String> editar(@PathVariable String id, Model model){
		Mono<Producto> productoMono = service.findById(id).doOnNext( p -> {
			log.info("Producto id" + p.getNombre());
		});
		
		model.addAttribute("titulo","Editar producto");
		model.addAttribute("boton","Editar");
		model.addAttribute("producto", productoMono);
				
		return Mono.just("form");
		
	}
	
	@PostMapping("/form")
	public Mono<String> guardar(@Valid Producto  producto, BindingResult result,Model model ){
		
		if(result.hasErrors()) {
			model.addAttribute("titulo","Errores en formulario producto");
			model.addAttribute("boton","Guardar");
			return Mono.just("form");
			
		}else {
		Mono<Categoria> categoria = service.findCategoriaById(producto.getCategoria().getId());

		return categoria.flatMap( c -> {
			
			if(producto.getCreateAt()==null) {
				producto.setCreateAt(new Date());
			}
		 
			producto.setCategoria(c);
			return service.save(producto);
		}).doOnNext( p ->{
			log.info("Producto guardado: " + p.getNombre() + " Id: " + p.getId());
		}).thenReturn("redirect:/listar?success=producto+guardado+con+exito");
		}
	}
	
	                  


	@GetMapping({ "/listar-data-driver" })
	public String listarDataDriver(Model model) {
		Flux<Producto> productos = service.findAllByNombreUpperCase().delayElements(Duration.ofSeconds(1));

		productos.subscribe(prod -> log.info(prod.getNombre()));

		model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos, 1));
		model.addAttribute("titulo", "Listado de productos");

		return "listar";
	}

	@GetMapping("/listar-full")
	public String listarFull(Model model) {
		Flux<Producto> productos = service.findAllConNombreUpperCaseRepeat();

		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "Listado de productos");

		return "listar";
	}

	@GetMapping("/listar-chunked")
	public String listarChunked(Model model) {
		Flux<Producto> productos = service.findAllConNombreUpperCaseRepeat();

		model.addAttribute("productos", productos);
		model.addAttribute("titulo", "Listado de productos");

		return "listar-chunked";
	}
}
