package com.springboot.webflux.app.models.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.springboot.webflux.app.models.dao.CategoriaDAO;
import com.springboot.webflux.app.models.dao.ProductoDAO;
import com.springboot.webflux.app.models.documents.Categoria;
import com.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class ProductoServiceImpl implements ProductoService {

	private static final Logger log = LoggerFactory.getLogger(ProductoServiceImpl.class);
		
	@Autowired
	private ProductoDAO dao;
	
	@Autowired
	private CategoriaDAO categoriaDAO;
	
	@Override
	public Flux<Producto> findAll() {
		// TODO Auto-generated method stub
		return dao.findAll();
	}

	@Override
	public Mono<Producto> findById(String id) {
		// TODO Auto-generated method stub
		return dao.findById(id);
	}

	@Override
	public Mono<Producto> save(Producto producto) {
		// TODO Auto-generated method stub
		return dao.save(producto);
	}

	@Override
	public Mono<Void> delete(Producto producto) {
		// TODO Auto-generated method stub
		return dao.delete(producto);
	}

	@GetMapping("/form/{id}")
	public Mono<String> editar(@PathVariable String id, Model model){
		Mono<Producto> productoMono = dao.findById(id).doOnNext( p-> {
			log.info("producto: " + p.getNombre());
		});
		
		model.addAttribute("Titulo", "Editar producto");
		model.addAttribute("producto", productoMono);
		
		return Mono.just("form");
	}
	
	
	@Override
	public Flux<Producto> findAllByNombreUpperCase() {
		// TODO Auto-generated method stub
		return dao.findAll().map(producto -> {
			
			producto.setNombre(producto.getNombre().toUpperCase());
			return producto;
		});
	}

	@Override
	public Flux<Producto> findAllConNombreUpperCaseRepeat() {
		// TODO Auto-generated method stub
		return findAllByNombreUpperCase().repeat(5000);
		
	}

	@Override
	public Flux<Categoria> findAllCategoria() {
		// TODO Auto-generated method stub
		return categoriaDAO.findAll();
	}

	@Override
	public Mono<Categoria> findCategoriaById(String id) {
		// TODO Auto-generated method stubsssss
		return categoriaDAO.findById(id);
	}

	@Override
	public Mono<Categoria> saveCategoria(Categoria categoria) {
		// TODO Auto-generated method stub
		return categoriaDAO.save(categoria);
	}
	
	

	
	
}
