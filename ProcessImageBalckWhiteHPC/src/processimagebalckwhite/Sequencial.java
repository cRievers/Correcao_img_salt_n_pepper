package processimagebalckwhite;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class Sequencial {

    public static int[][] lerPixels(String caminho) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new File(caminho));
            int largura = bufferedImage.getWidth(null);
            int altura = bufferedImage.getHeight(null);

            int[][] pixels = new int[largura][altura];
            for (int i = 0; i < largura; i++) {
                for (int j = 0; j < altura; j++) {
                    //normalizando de forma simplificada para imagem escala de cinza (é esperado ocorrer "clareamento")
                    float vermelho = new Color(bufferedImage.getRGB(i, j)).getRed();
                    float verde = new Color(bufferedImage.getRGB(i, j)).getGreen();
                    float azul = new Color(bufferedImage.getRGB(i, j)).getBlue();
                    int escalaCinza = (int) (vermelho + verde + azul) / 3;

                    pixels[i][j] = escalaCinza;
                }
            }
            return pixels;
        } catch (IOException ex) {
            System.err.println("Erro no caminho indicado pela imagem");
        }
        return null;
    }

    public static void gravarPixels(String caminhoGravar, int pixels[][]) {
        caminhoGravar = caminhoGravar
                .replace(".png", "_modificado.png")
                .replace(".jpg", "_modificado.jpg");
        int largura = pixels.length;
        int altura = pixels[0].length;

        BufferedImage imagem = new BufferedImage(largura, altura, BufferedImage.TYPE_BYTE_GRAY);
        //transformando a mat. em um vetor de bytes
        byte bytesPixels[] = new byte[largura * altura];
        for (int x = 0; x < largura; x++) {
            for (int y = 0; y < altura; y++) {
                bytesPixels[y * (largura) + x] = (byte) pixels[x][y];
            }
        }
        //copiando todos os bytes para a nova imagem
        imagem.getRaster().setDataElements(0, 0, largura, altura, bytesPixels);
        //criamos o arquivo e gravamos os bytes da imagem nele
        File ImageFile = new File(caminhoGravar);
        try {
            ImageIO.write(imagem, "png", ImageFile);
            System.out.println("Nova Imagem dispon. em: " + caminhoGravar);
        } catch (IOException e) {
            System.err.println("Erro no caminho indicado pela imagem");
        }
    }
    
    /**
     * Varre toda a matriz de pixels à procura de pontos pretos (0)
     * ou brancos (255) [do efeito salt and pepper] e substitui pelos pixels ao redor.
     * Usa a técnica de média dos valores em uma máscara 3x3.
     *
     * @return Retorna uma matriz de pixels com os pontos corrigidos.
     */
    public static int[][] corrigirImagem(int imgMat[][]){
        int ultimaLinha = imgMat.length - 1;
        int ultimaColuna = imgMat[0].length - 1;
        
        for (int linha = 0; linha <= ultimaLinha; linha++) { //percorre as linhas
            for (int coluna = 0; coluna <= ultimaColuna; coluna++) { //percorre as colunas
                int pixel = imgMat[linha][coluna];
                if (pixel == 0 || pixel == 255) { //testa se é preto ou branco
                    int soma = 0;
                    int div = 0;
                    for (int l = -1; l <= 1; l++) { //roda a mini matriz 3x3 (máscara)
                        for (int c = -1; c <= 1; c++) {
                            int lin = linha + l;
                            int col = coluna + c;
                            if ( !(lin < 0 || col < 0) &&
                                    (lin <= ultimaLinha && col <= ultimaColuna)) { //se a posição for válida
                                int pix = imgMat[lin][col];
                                if (!(pix == 0 || pix == 255)) { //se n for preto/branco 
                                    div++;
                                    soma += pix;
                                }
                            }
                        }
                        if (div != 0) {
                            int novaCor = soma / div;
                            imgMat[linha][coluna] = novaCor;
                        }
                    }
                }
            }
        }
        return imgMat;
    }
    
    //recebe o endereco do diretório que contém as imagens a serem modificadas
    public static void main(String args[]){

        File directory = new File(args[0]);
        File imagesFile[] = directory.listFiles();
        
        long tempoI = System.currentTimeMillis();
        
        //imagens que precisam ser corrigidas
        for(File img : imagesFile){
            int imgMat[][] = lerPixels(img.getAbsolutePath());
            
            imgMat = corrigirImagem(imgMat);
            
            //grava nova imagem com as correções
            if(imgMat != null){
                gravarPixels(img.getAbsolutePath(), imgMat);
            }
        }
        long tempoF = System.currentTimeMillis();
        double tempoT = (tempoF - tempoI) / 1000.0;
        System.out.printf("Tempo total: %.3f segundos.\n", tempoT);
    }
}
