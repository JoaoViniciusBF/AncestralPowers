package dev.joaq.ancestralpowers.skin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class SkinManager {
    private static final Gson GSON = new Gson();
    private static final Map<String, SkinData> SKINS = new HashMap<>();
    private static final Map<String, String> PENDING_UPLOADS = new HashMap<>();
    
    public static class SkinData {
        public String value;
        public String signature;
        public String source;
        
        public SkinData(String value, String signature, String source) {
            this.value = value;
            this.signature = signature;
            this.source = source;
        }
    }
    
    public static void loadSkins(MinecraftServer server) {
        SKINS.clear();
        
        File worldFolder = server.getRunDirectory();
        File skinsFolder = new File(worldFolder, "skins");
        
        if (!skinsFolder.exists()) {
            skinsFolder.mkdirs();
            createExampleFiles(skinsFolder);
            return;
        }
        
        File[] files = skinsFolder.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            String fileName = file.getName().toLowerCase();
            String skinName = file.getName().substring(0, file.getName().lastIndexOf('.'));
            
            try {
                if (fileName.endsWith(".json")) {
                    loadJsonSkin(file, skinName);
                } else if (fileName.endsWith(".png")) {
                    loadPngSkin(file, skinName, skinsFolder);
                }
            } catch (Exception e) {
                System.err.println("Failed to load skin: " + file.getName());
                e.printStackTrace();
            }
        }
        
        System.out.println("Loaded " + SKINS.size() + " custom skins");
    }
    
    private static void loadJsonSkin(File file, String skinName) throws Exception {
        FileReader reader = new FileReader(file);
        JsonObject json = GSON.fromJson(reader, JsonObject.class);
        reader.close();
        
        if (json.has("value") && json.has("signature")) {
            String value = json.get("value").getAsString();
            String signature = json.get("signature").getAsString();
            SKINS.put(skinName.toLowerCase(), new SkinData(value, signature, "json"));
            System.out.println("Loaded JSON skin: " + skinName);
        }
    }
    
    private static void loadPngSkin(File pngFile, String skinName, File skinsFolder) throws Exception {
        File jsonFile = new File(skinsFolder, skinName + ".json");
        
        if (jsonFile.exists()) {
            System.out.println("JSON already exists for " + skinName + ", using JSON version");
            return;
        }
        
        BufferedImage image = ImageIO.read(pngFile);
        
        if (image == null) {
            throw new IOException("Failed to read PNG file");
        }
        
        if ((image.getWidth() != 64 || image.getHeight() != 64) && 
            (image.getWidth() != 64 || image.getHeight() != 32)) {
            System.err.println("Invalid skin dimensions for " + skinName + ": " + 
                             image.getWidth() + "x" + image.getHeight() + 
                             " (expected 64x64 or 64x32)");
            return;
        }
        
        System.out.println("PNG skin detected: " + skinName + ", attempting to generate JSON...");
        
        try {
            SkinData skinData = uploadToMineskin(pngFile);
            if (skinData != null) {
                JsonObject json = new JsonObject();
                json.addProperty("value", skinData.value);
                json.addProperty("signature", skinData.signature);
                json.addProperty("source", "mineskin_auto");
                
                Files.writeString(jsonFile.toPath(), GSON.toJson(json));
                
                SKINS.put(skinName.toLowerCase(), skinData);
                System.out.println("Successfully converted PNG to JSON: " + skinName);
            } else {
                PENDING_UPLOADS.put(skinName.toLowerCase(), pngFile.getAbsolutePath());
                System.out.println("PNG skin queued for manual conversion: " + skinName);
            }
        } catch (Exception e) {
            PENDING_UPLOADS.put(skinName.toLowerCase(), pngFile.getAbsolutePath());
            System.err.println("Failed to auto-convert PNG, will require manual conversion: " + skinName);
        }
    }
    
    private static SkinData uploadToMineskin(File pngFile) {
        try {
            URL url = new URL("https://api.mineskin.org/generate/upload");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary");
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            try (OutputStream os = conn.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true)) {
                
                writer.append("--" + boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"skin.png\"").append("\r\n");
                writer.append("Content-Type: image/png").append("\r\n");
                writer.append("\r\n").flush();
                
                Files.copy(pngFile.toPath(), os);
                os.flush();
                
                writer.append("\r\n");
                writer.append("--" + boundary + "--").append("\r\n").flush();
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                
                JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
                if (json.has("data")) {
                    JsonObject data = json.getAsJsonObject("data");
                    if (data.has("texture")) {
                        JsonObject texture = data.getAsJsonObject("texture");
                        String value = texture.get("value").getAsString();
                        String signature = texture.get("signature").getAsString();
                        return new SkinData(value, signature, "mineskin");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to upload to Mineskin: " + e.getMessage());
        }
        return null;
    }
    
    private static void createExampleFiles(File skinsFolder) {
        try {
            File exampleJson = new File(skinsFolder, "example.json");
            String exampleJsonContent = """
                {
                    "value": "ewogICJ0aW1lc3RhbXAiIDogMTcyMjEzNjgyOTkwOCwKICAicHJvZmlsZUlkIiA6ICJmZDYwZjM2ZjViZGI0ZDU2YjhjZDkwNGYzMGRkMTZhMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJSaGFzdF9SYWJiZXIiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDIzMzU1YmNmZDI2NGM1NjJhOWVlOTBkZjIzMGM2ZGQ0Mjk0YjBkNGM2NTIxZmY1YzhkYTllOWI4NmM2MjRiYiIKICAgIH0KICB9Cn0=",
                    "signature": "ZXhhbXBsZV9zaWduYXR1cmVfaGVyZQ=="
                }
                """;
            Files.writeString(exampleJson.toPath(), exampleJsonContent);
            
            File readmeFile = new File(skinsFolder, "README.txt");
            String readme = """
                Como adicionar skins customizadas:
                
                MÉTODO RECOMENDADO - Direto do Minecraft.tools:
                1. Vá em https://minecraft.tools/en/skin.php?skin=SEU_NICK_AQUI
                2. Clique com botão direito na imagem da skin
                3. Salvar imagem como... na pasta skins/ (exemplo: steve.png)
                4. Use /reloadskins no jogo
                5. O sistema tentará converter automaticamente para JSON
                6. Use /skin steve para aplicar
                
                MÉTODO ALTERNATIVO - Upload manual:
                1. Acesse https://mineskin.org/
                2. Faça upload da sua skin PNG (64x64 ou 64x32)
                3. Aguarde processar
                4. Copie o "value" e "signature" gerados
                5. Crie um arquivo .json nesta pasta:
                   {
                     "value": "COLE_AQUI_O_VALUE",
                     "signature": "COLE_AQUI_A_SIGNATURE"
                   }
                6. Use /reloadskins e depois /skin nome
                
                BAIXAR SKIN DE JOGADOR:
                - https://minecraft.tools/en/skin.php?skin=NICK
                - https://minotar.net/skin/NICK
                - https://crafatar.com/skins/UUID
                
                Exemplos:
                - joaq111.png -> /skin joaq111
                - steve.json -> /skin steve
                
                Nota: Conversão automática PNG→JSON pode levar alguns segundos.
                      Se falhar, use o método manual via Mineskin.org
                """;
            Files.writeString(readmeFile.toPath(), readme);
            
            System.out.println("Created example files in: " + skinsFolder.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static SkinData getSkin(String name) {
        return SKINS.get(name.toLowerCase());
    }
    
    public static Map<String, SkinData> getAllSkins() {
        return new HashMap<>(SKINS);
    }
    
    public static boolean hasSkin(String name) {
        return SKINS.containsKey(name.toLowerCase());
    }
    
    public static boolean isPending(String name) {
        return PENDING_UPLOADS.containsKey(name.toLowerCase());
    }
    
    public static String getPendingPath(String name) {
        return PENDING_UPLOADS.get(name.toLowerCase());
    }
}
