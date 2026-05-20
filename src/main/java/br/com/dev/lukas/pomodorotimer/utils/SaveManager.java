package br.com.dev.lukas.pomodorotimer.utils;

import br.com.dev.lukas.pomodorotimer.model.User;
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

    public static void saveData(){
        Gson gson = new Gson();
        File saveFolder = getSaveFolder();

        File file = new File(saveFolder,"userSave.json");

        try(FileWriter writer = new FileWriter(file)){

            gson.toJson(User.getInstance(), writer);
            System.out.println("Dados salvos");

        } catch (IOException e) {
            System.out.println("Erro ao salvar"+e.getMessage());;
        }
    }

    public static void loadData(){
        File saveFolder = getSaveFolder();
        File file = new File(saveFolder,"userSave.json");

        if(!file.exists()){
            System.out.println("Nenhum save foi encontrado. Iniciando um novo save.");
            return;
        }

        Gson gson = new Gson();

        try(FileReader reader = new FileReader(file)){
            User userSaved = gson.fromJson(reader, User.class);

            if(userSaved != null && userSaved.getUserPokemons() != null){
                User.getInstance().setUserPokemons(userSaved.getUserPokemons());
                System.out.println("Dados carregados");
            }

        }catch(IOException e){
            System.out.println("Erro ao ler o save"+e.getMessage());
        }
    }
}
