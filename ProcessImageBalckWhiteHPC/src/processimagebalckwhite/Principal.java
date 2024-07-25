package processimagebalckwhite;

import java.io.File;

public class Principal {
    public static void main(String[] args) {
        
        int numThreads = Runtime.getRuntime().availableProcessors();
        Trabalhador[] processadores = new Trabalhador[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            processadores[i] = new Trabalhador();
        }
        
        File directory = new File("D:\\Projetos\\BachelorDegreeIT\\Arquitetura e Organização de Computadores" +
                "\\projeto e arquivos para o problema de imagens\\Imagens\\modificadas");
        File imagesFile[] = directory.listFiles();
        
        Trabalhador.addImagem(imagesFile);
        
        
        
    }
}
