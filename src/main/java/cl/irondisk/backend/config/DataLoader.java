package cl.irondisk.backend.config;

import cl.irondisk.backend.model.Categoria;
import cl.irondisk.backend.model.Producto;
import cl.irondisk.backend.model.Rol;
import cl.irondisk.backend.model.Usuario;
import cl.irondisk.backend.repository.CategoriaRepository;
import cl.irondisk.backend.repository.ProductoRepository;
import cl.irondisk.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// Carga los datos de ejemplo de la tienda al iniciar, solo si las tablas estan vacias
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final UsuarioRepository usuarioRepo;
    private final CategoriaRepository categoriaRepo;
    private final ProductoRepository productoRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (usuarioRepo.count() == 0) {
            cargarUsuarios();
        }
        if (categoriaRepo.count() == 0 && productoRepo.count() == 0) {
            cargarCatalogo();
        }
    }

    // Una cuenta de cada rol, las claves se guardan con BCrypt
    private void cargarUsuarios() {
        usuarioRepo.save(usuario("190110222", "Bastián", "Jofré", "ba.jofre@duoc.cl", "admin123", Rol.ADMIN,
                null, "Metropolitana de Santiago", "Providencia", "Av. Providencia 2124, local 15"));
        usuarioRepo.save(usuario("181245875", "Renato", "Colipe Lienan", "re.colipe@duoc.cl", "admin123", Rol.ADMIN,
                null, "Metropolitana de Santiago", "Santiago", "Alameda 1234"));
        usuarioRepo.save(usuario("204567891", "Diego", "Pérez Mora", "diego.perez@duoc.cl", "vendedor1", Rol.VENDEDOR,
                null, "Biobío", "Concepción", "Barros Arana 890"));
        usuarioRepo.save(usuario("123456785", "Camila", "Soto Rojas", "camila.soto@gmail.com", "cliente1", Rol.CLIENTE,
                LocalDate.of(1998, 5, 12), "Valparaíso", "Viña del Mar", "Calle Valparaíso 567"));
        log.info("Usuarios de ejemplo cargados");
    }

    // Las dos categorias y los 22 discos de la tienda
    private void cargarCatalogo() {
        Categoria metal = categoriaRepo.save(Categoria.builder().nombre("Metal")
                .descripcion("Del heavy clásico al thrash y el power metal.").imagen("img/fondo-concierto1.jpg").build());
        Categoria cumbia = categoriaRepo.save(Categoria.builder().nombre("Cumbia")
                .descripcion("Cumbia villera, tropical y romántica.").imagen("img/cumbia-villera.webp").build());

        producto("IM-001", "The Number of the Beast", "Iron Maiden", "Vinilo", 32990, 12, 3, metal, "img/maiden-beast.png", 15, "Clásico de 1982 con Run to the Hills y Hallowed Be Thy Name.");
        producto("IM-002", "Powerslave", "Iron Maiden", "CD", 16990, 8, 3, metal, "img/iron2.png", 0, "Álbum de 1984 con Aces High y Rime of the Ancient Mariner.");
        producto("MT-001", "and justice for all", "Metallica", "Vinilo", 35990, 5, 3, metal, "img/meta1.png", 0, "Thrash metal progresivo de 1988.");
        producto("MT-002", "ride the lightning", "Metallica", "CD", 18990, 10, 3, metal, "img/meta2.png", 20, "El segundo disco de Metallica, de 1984.");
        producto("MG-001", "Rust in Peace", "Megadeth", "Vinilo", 29990, 6, 2, metal, "img/mega1.png", 0, "Obra maestra del thrash técnico, 1990.");
        producto("MG-002", "Peace Sells", "Megadeth", "CD", 15990, 2, 3, metal, "img/mega2.png", 0, "Segundo álbum de Megadeth, 1986.");
        producto("AN-001", "Among the Living", "Anthrax", "Vinilo", 27990, 7, 2, metal, "img/ant1.png", 0, "Thrash neoyorquino de 1987.");
        producto("AN-002", "Spreading the Disease", "Anthrax", "CD", 14990, 9, 3, metal, "img/ant2.png", 0, "Primer álbum con Joey Belladonna, 1985.");
        producto("SL-001", "Reign in Blood", "Slayer", "Vinilo", 31990, 4, 3, metal, "img/sla1.png", 0, "Menos de 30 minutos de thrash extremo, 1986.");
        producto("SL-002", "Show No Mercy", "Slayer", "Cassette", 11990, 1, 2, metal, "img/sla2.png", 25, "Debut de Slayer en 1983.");
        producto("HW-001", "Keeper of the Seven Keys I", "Helloween", "CD", 15990, 6, 2, metal, "img/hello1.png", 0, "Power metal que definió el género, 1987.");
        producto("HW-002", "Keeper of the Seven Keys II", "Helloween", "Vinilo", 28990, 5, 2, metal, "img/hello2.png", 0, "Continuación del clásico, 1988.");

        producto("PC-001", "Arriba las Manos", "Pibes Chorros", "CD", 9990, 11, 3, cumbia, "img/pibe1.png", 10, "Cumbia villera para bailar.");
        producto("PC-002", "En vivo...Hasta la muerte", "Pibes Chorros", "Cassette", 10990, 3, 3, cumbia, "img/pibe2.png", 0, "Registro en vivo en formato cassette.");
        producto("DG-001", "La gota que rebasó el vaso", "Damas Gratis", "CD", 10990, 8, 2, cumbia, "img/damas1.png", 0, "Cumbia villera de Damas Gratis.");
        producto("DG-002", "100% Negro Cumbiero", "Damas Gratis", "CD", 11990, 6, 2, cumbia, "img/damas2.png", 0, "Otro clásico de Damas Gratis.");
        producto("GA-001", "En vivo alegria pan de azucar", "Grupo Alegría", "Cassette", 5990, 4, 2, cumbia, "img/alegria1.png", 0, "Cumbia tropical en vivo.");
        producto("GA-002", "El nuevo tropical", "Grupo Alegría", "CD", 8990, 9, 3, cumbia, "img/alegria2.png", 30, "Cumbia tropical de Grupo Alegría.");
        producto("TK-001", "Grandes Éxitos", "Tropi-ka'l Sound", "CD", 9990, 7, 2, cumbia, "img/tropikal1.png", 0, "Recopilación de grandes éxitos.");
        producto("TK-002", "Tropi-ka'l Sound", "Tropi-ka'l Sound", "Cassette", 7990, 2, 3, cumbia, "img/tropikal2.png", 0, "Disco homónimo en cassette.");
        producto("AZ-001", "Cumbia Nena", "Amar Azul", "CD", 12990, 10, 2, cumbia, "img/amar1.png", 15, "Cumbia romántica de Amar Azul.");
        producto("AZ-002", "Dime Azul", "Amar Azul", "CD", 11990, 5, 2, cumbia, "img/amar2.png", 0, "Otro éxito de Amar Azul.");
        log.info("Categorías y productos de ejemplo cargados");
    }

    private Usuario usuario(String run, String nombre, String apellidos, String correo, String password, Rol rol,
                            LocalDate fechaNacimiento, String region, String comuna, String direccion) {
        return Usuario.builder().run(run).nombre(nombre).apellidos(apellidos).correo(correo)
                .password(encoder.encode(password)).rol(rol).fechaNacimiento(fechaNacimiento)
                .region(region).comuna(comuna).direccion(direccion).build();
    }

    private void producto(String codigo, String nombre, String artista, String formato, int precio, int stock,
                          int stockCritico, Categoria categoria, String imagen, int oferta, String descripcion) {
        productoRepo.save(Producto.builder().codigo(codigo).nombre(nombre).artista(artista).formato(formato)
                .precio(precio).stock(stock).stockCritico(stockCritico).categoria(categoria).imagen(imagen)
                .ofertaPorcentaje(oferta).descripcion(descripcion).build());
    }
}
