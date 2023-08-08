package com.springboot.webflux.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.springboot.webflux.app.models.dao.ProductoDAO;
import com.springboot.webflux.app.models.documents.Categoria;
import com.springboot.webflux.app.models.documents.Producto;
import com.springboot.webflux.app.models.services.ProductoService;
import com.springboot.webflux.app.models.services.ProductoServiceImpl;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);

	@Autowired
	private ProductoService service;

	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		mongoTemplate.dropCollection("productos").subscribe();
		mongoTemplate.dropCollection("categorias").subscribe();

		Categoria electronico = new Categoria("electronico");
		Categoria deporte = new Categoria("deporte");
		Categoria computacion = new Categoria("computacion");
		Categoria mueble = new Categoria("mueble");

		Flux.just(electronico, deporte, computacion, mueble)
		.flatMap( service::saveCategoria)
		.doOnNext(c -> {
			log.info("categoria creada");
		}).thenMany(
			Flux.just(new Producto("TV Panasonic LCD", 324.32, electronico),
				new Producto("TV Panasonic LCD", 324.32, deporte),
				new Producto("TV Panasonic LCD", 324.32, computacion), 
				new Producto("TV Panasonic LCD", 324.12, mueble)
				)
			.flatMap(producto -> {
					producto.setCreateAt(new Date());
					return service.save(producto);
				})
				)
				.subscribe(producto -> log.info("asd") );
	}
}
