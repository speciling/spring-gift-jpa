package gift.doamin.product.service;

import gift.doamin.product.entity.Product;
import gift.doamin.product.repository.JpaProductRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {
    private final JpaProductRepository productRepository;

    public ProductService(JpaProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product create(Product product) {
        if (product.getName().contains("카카오")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "'카카오'가 포함된 문구는 담당 MD와 협의한 경우에만 사용할 수 있습니다.");
        }

        return productRepository.save(product);
    }

    public List<Product> readAll() {
        return productRepository.findAll();
    }

    public Product readOne(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Product update(Long userId, Product product, boolean isSeller) {
        Long id = product.getId();

        Product target = productRepository.findById(id)
            .orElseThrow(() -> {
                create(product);
                return new ResponseStatusException(HttpStatus.CREATED);
            });

        checkAuthority(userId, target, isSeller);


        product.setUserId(target.getUserId());
        System.out.println("userId: " + product.getUserId());
        return productRepository.save(product);
    }

    public void delete(Long userId, Long id, boolean isSeller) {
        Product target = productRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        checkAuthority(userId, target, isSeller);

        productRepository.deleteById(id);
    }

    private void checkAuthority(Long userId, Product target, boolean isSeller) {
        if (isSeller && !target.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

    }
}
