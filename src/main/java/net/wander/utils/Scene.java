package net.wander.utils;

import net.wander.objects.GameObject;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Одна сцена (уровень/меню и т.п.).
 * Хранит список GameObject и умеет их апдейтить/рисовать.
 */
public class Scene {

    private final String name;
    private final List<GameObject> objects = new ArrayList<>();

    // движок, которому эта сцена принадлежит
    private SimpleGame game;

    public Scene(String name) {
        this.name = name;
    }

    void setGame(SimpleGame game) {
        this.game = game;
    }

    public SimpleGame getGame() {
        return game;
    }

    public String getName() {
        return name;
    }

    /** Добавить объект на сцену. */
    public void addObject(GameObject obj) {
        if (obj != null) {
            objects.add(obj);
        }
    }

    /** Удалить объект со сцены. */
    public void removeObject(GameObject obj) {
        objects.remove(obj);
    }

    /** Прочитать список объектов (только для чтения). */
    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    /** Вызывается движком при входе на сцену. */
    public void onEnter() {
    }

    /** Вызывается движком при выходе со сцены. */
    public void onExit() {
    }

    /** Обновление логики всех объектов сцены. */
    public void update(double dt) {
        // Копия списка на случай, если внутри update кто‑то будет добавлять/удалять объекты
        List<GameObject> snapshot = new ArrayList<>(objects);
        for (GameObject obj : snapshot) {
            obj.update(dt);
        }
    }

    /** Отрисовка всех объектов сцены. */
    public void render(Graphics2D g) {
        for (GameObject obj : objects) {
            obj.render(g);
        }
    }

    /** Удобный метод: перейти на другую сцену по имени. */
    public void gotoScene(String name) {
        if (game != null) {
            game.gotoScene(name);
        }
    }

    /** Удобный метод: перейти на другую сцену по ссылке. */
    public void gotoScene(Scene scene) {
        if (game != null && scene != null) {
            game.gotoScene(scene);
        }
    }
}