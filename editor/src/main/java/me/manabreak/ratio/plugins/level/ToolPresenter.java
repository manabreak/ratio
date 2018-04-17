package me.manabreak.ratio.plugins.level;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import me.manabreak.ratio.common.mvp.MvpPresenter;

public class ToolPresenter extends MvpPresenter<ToolView> {

    private final PublishSubject<Integer> toolSizeSubject = PublishSubject.create();

    public void toolSizeChanged(int s) {
        int toolSize = 1 << s;
        view.toolSizeChanged(toolSize);
        toolSizeSubject.onNext(toolSize);
    }

    public Observable<Integer> getToolSizeObservable() {
        return toolSizeSubject;
    }
}
