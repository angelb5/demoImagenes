package com.example.demoimages;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Controller
@RequestMapping("/")
public class ImageController {

    @Value("${uploadDir}")
    private String uploadFolder;

    @Autowired
    ImageRepository imageRepository;

    @GetMapping(value = {"", "/","/lista"})
    public String listaJuegos (Model model){

        List<Image> imageList = imageRepository.findAll();
        model.addAttribute("imageList", imageList);
        return "/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoJuegos(@ModelAttribute("image") Image image, Model model) {
        return "/editarFrm";
    }

    @PostMapping("/guardar")
    public String guardarJuego(@ModelAttribute("image") Image image, @RequestParam("file") MultipartFile imagen,
                               RedirectAttributes attr){
        try{
            byte[] imagenData = imagen.getBytes();
            image.setImage(imagenData);
            if (image.getId() == 0 ) {
                attr.addFlashAttribute("msg", "Imagen creada exitosamente");
                imageRepository.save(image);
                return "redirect:/lista";
            } else {
                imageRepository.save(image);
                attr.addFlashAttribute("msg", "Imagen actualizada exitosamente");
                return "redirect:/lista";
            }
        }catch(Exception e){
            e.printStackTrace();
            return "redirect:/lista";
        }

    }
    @GetMapping("/editar")
    public String editarJuego(@ModelAttribute("image") Image image, Model model,
                              @RequestParam(value="id", required = false, defaultValue = "1") int id) {
        Optional<Image> imageOptional = imageRepository.findById(id);
        if(imageOptional.isPresent()){
            Image imageDB = imageOptional.get();
            model.addAttribute("image",imageDB);
            return "editarFrm";
        }
        return "redirect:/lista";
    }

    @GetMapping("/borrar")
    public String borrarDistribuidora(@RequestParam(value = "id",defaultValue = "1") int id , RedirectAttributes attr ){
        Optional<Image> opt = imageRepository.findById(id);
        if (opt.isPresent()) {
            imageRepository.deleteById(id);
            attr.addFlashAttribute("msg", "Imagen borrada exitosamente");
        }
        return "redirect:/lista";
    }

    @GetMapping("/image/display/{id}")
    @ResponseBody
    void showImage(@PathVariable("id") int id, HttpServletResponse response, Optional<Image> image)
            throws ServletException, IOException {
        image = imageRepository.findById(id);
        if(image.isPresent()){
            response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
            response.getOutputStream().write(image.get().getImage());
            response.getOutputStream().close();
        }

    }
}
