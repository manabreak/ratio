package me.manabreak.ratio.plugins.files;

import com.badlogic.gdx.files.FileHandle;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import me.manabreak.ratio.editor.EditorPlugin;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class FileWatcherPlugin extends EditorPlugin {

    private WatchService watchService;

    public FileWatcherPlugin() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
    }

    public Observable<String> observe(FileHandle fh) {
        final Observable<String> o = Observable.create(emitter -> {
            Path path = FileSystems.getDefault().getPath(fh.isDirectory() ? fh.path() : fh.parent().path());
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            System.out.println("Registered to watch " + fh.path());

            while (true) {
                try {
                    final WatchKey take = watchService.take();
                    for (WatchEvent<?> event : take.pollEvents()) {
                        Path p = (Path) event.context();
                        if (p.endsWith(fh.name())) {
                            System.out.println("Changed file -> " + p.toString());
                            emitter.onNext(fh.name());
                        }
                    }
                    take.reset();
                } catch (Exception e) {
                    emitter.onError(e);
                    break;
                }
            }
        });
        return o.subscribeOn(Schedulers.io()).debounce(50, TimeUnit.MILLISECONDS);
    }
}
