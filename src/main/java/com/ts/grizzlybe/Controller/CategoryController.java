package com.ts.grizzlybe.Controller;

import com.ts.grizzlybe.Client.ProductClient;
import com.ts.grizzlybe.Model.Category;
import com.ts.grizzlybe.Repository.CategoryRepository;
import com.ts.grizzlybe.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path="/")
@CrossOrigin
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductClient productClient;



    @GetMapping
    public @ResponseBody ResponseEntity<HashMap<String, Iterable<Category>>> getAllCategories() {
        Iterable<Category> categories = categoryRepository.findAll();
        // This returns a JSON or XML with the users

        // create hash map
        HashMap<String, Iterable<Category>> ContentMap = new HashMap<>();

        ContentMap.put("content", categories);

        return new ResponseEntity<>(ContentMap, HttpStatus.OK);
    }

//    Method used for the global search to find categories by name
    @GetMapping(value = "/allByLen")
    public List<Category> getAllCategoriesByLen(int size, String search){

        return categoryService.findNameBySearchTerm(search, new PageRequest(0, size)).getContent();
    }

    @GetMapping(value = "/page")
    public Page<Category> findBySearchTerm(@RequestParam("search") String searchTerm, Pageable pageable) {

        Page <Category> categoryPage = categoryService.findBySearchTerm(searchTerm, pageable);

        for(Category category: categoryPage) {
            try {
                ResponseEntity<Long> response = productClient.productCount(category.getId());
                if (response.getStatusCodeValue() == 200) {
                    category.setCount(response.getBody());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return categoryPage;
    }

    @PostMapping(path="/add", headers = "Content-Type=application/json") // Map ONLY GET Requests
    public ResponseEntity addNewCategory (@RequestBody Category category) {

        categoryRepository.save(category);

        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }

    @PostMapping(path="/add") // Map ONLY GET Requests
    public ResponseEntity addNewCategory (@RequestParam String name, @RequestParam String description) {

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        categoryRepository.save(category);

        return new ResponseEntity<>(category, HttpStatus.CREATED);
    }





   /* @GetMapping("/sort")
    public @ResponseBody ResponseEntity<Iterable<Category>> getAllUsersSort(@RequestParam String name ) {
        if(name.equals("A_Z") ) {
            Iterable<Category> categories = categoryRepository.sortByA_Z(name);
            return new ResponseEntity<>(categories, HttpStatus.OK);
        }
        else if(name.equals("Z_A"))
        {
            Iterable<Category> categories = categoryRepository.sortByZ_A(name);
            return new ResponseEntity<>(categories, HttpStatus.OK);
        }
        else if(name.equals("IdAsc"))
        {
            Iterable<Category> categories = categoryRepository.sortByIdAsc(name);
            return new ResponseEntity<>(categories, HttpStatus.OK);
        }
        else if(name.equals("IdDesc"))
        {
            Iterable<Category> categories = categoryRepository.sortByIdDesc(name);
            return new ResponseEntity<>(categories, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // This returns a JSON or XML with the users

    } */

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity getCategory(@PathVariable Long id)
    {
        Optional<Category> category =  categoryRepository.findById(id);
        if (!category.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        else
        {
            return new ResponseEntity<>(category,HttpStatus.OK);
        }

    }

    @PostMapping("/getBatch")
    @ResponseBody
    public ResponseEntity<HashMap<Long, Category>> getCategoriesBatch(@RequestBody Long[] catIds) {
        // verify ids to fetch are passed
        if (catIds.length < 1) return ResponseEntity.notFound().build();

        // fetch categories in batch from database
        List<Category> categories = categoryRepository.findBatchCategories(catIds);

        // create hashmap that will be returned
        HashMap<Long, Category> batched = new HashMap<>();

        // add categories into the hashmap
        for (Category cat : categories) { batched.put(cat.getId(), cat);}

        // return the appropriate response
        if (batched.size() > 0) return new ResponseEntity<>(batched, HttpStatus.OK);
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity editCategory(@PathVariable long id ,@RequestBody Category category )
    {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (!categoryOptional.isPresent())
            return ResponseEntity.notFound().build();

        category.setId(id);

        categoryRepository.save(category);

        // return ResponseEntity.noContent().build();
        return new ResponseEntity<>("Category @{" + id + "} updated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteCategory(@PathVariable long id) {
        categoryRepository.deleteById(id);

        return new ResponseEntity<>("Deleted user@{" + id + "} successfully", HttpStatus.OK);
    }

}