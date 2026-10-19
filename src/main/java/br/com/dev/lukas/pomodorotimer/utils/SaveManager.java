package br.com.dev.lukas.pomodorotimer.utils;

import br.com.dev.lukas.pomodorotimer.model.User;
import br.com.dev.lukas.pomodorotimer.model.UserSettings;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SaveManager {

    private static File getSaveFolder() {
        String path = System.getProperty("user.home");

        File saveFolder = new File(path,"PomondoroSave");
        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        }
        return saveFolder;
    }
    public  static void saveData(){
        saveData(getSaveFolder());
    }
    public static void saveData(File saveFolder){
        Gson gson = new Gson();
        File file = new File(saveFolder,"userSave.json");

        try(FileWriter writer = new FileWriter(file)){
            gson.toJson(User.getInstance(), writer);
        } catch (IOException e) {
            System.out.println("Erro ao salvar"+e.getMessage());;
        }
    }

    public static void loadData(){
        loadData(getSaveFolder());
    }

    public static void loadData(File saveFolder){
        File file = new File(saveFolder,"userSave.json");
        if(!file.exists()){
            return;
        }
        Gson gson = new Gson();
        try(FileReader reader = new FileReader(file)){
            User userSaved = gson.fromJson(reader, User.class);

            if(userSaved != null && userSaved.getUserPokemons() != null){
                User.getInstance().setUserPokemons(userSaved.getUserPokemons());

                if (userSaved.getSettings() != null) {
                    User.getInstance().setUserSettings(userSaved.getSettings());
                } else {
                    User.getInstance().setUserSettings(new UserSettings());
                }

                System.out.println("Dados carregados");
            }

        }catch(IOException e){
            System.out.println("Erro ao ler o save"+e.getMessage());
        }


    }
}
