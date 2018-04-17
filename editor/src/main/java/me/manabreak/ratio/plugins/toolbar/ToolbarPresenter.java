package me.manabreak.ratio.plugins.toolbar;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import me.manabreak.ratio.common.mvp.MvpPresenter;
import me.manabreak.ratio.editor.EditorView;
import me.manabreak.ratio.plugins.camera.CameraSnapMode;

public class ToolbarPresenter extends MvpPresenter<ToolbarUi> {

    private final PublishSubject<Tool> toolSubject = PublishSubject.create();
    private final PublishSubject<CameraSnapMode> cameraSnapSubject = PublishSubject.create();
    private final PublishSubject<Boolean> cameraProjectionSubject = PublishSubject.create();
    private final EditorView mainView;
    private Tool tool = Tool.NONE;

    public ToolbarPresenter(EditorView mainView) {
        this.mainView = mainView;
    }

    private void changeTool(Tool tool) {
        if (this.tool == tool) {
            this.tool = Tool.NONE;
        } else {
            this.tool = tool;
        }
        toolSubject.onNext(tool);
    }

    public void blockToolSelected() {
        changeTool(Tool.BLOCK);
    }

    public void floorToolSelected() {
        changeTool(Tool.FLOOR);
    }

    public void paintToolSelected() {
        changeTool(Tool.PAINT);
    }

    public void eraseToolSelected() {
        changeTool(Tool.ERASE);
    }

    public void selectToolSelected() {
        changeTool(Tool.SELECT);
    }

    public void createObjectClicked() {

    }

    public PublishSubject<Tool> getToolSubject() {
        return toolSubject;
    }

    public void toggleLeftPanel() {
        mainView.toggleLeftPanel();
    }

    public void toggleRightPanel() {
        mainView.toggleRightPanel();
    }

    public void setCameraSnap(boolean checked) {
        cameraSnapSubject.onNext(checked ? CameraSnapMode.TOPDOWN : CameraSnapMode.NONE);
    }

    public Observable<CameraSnapMode> getCameraSnapObservable() {
        return cameraSnapSubject;
    }

    public void setCameraMode(boolean checked) {
        cameraProjectionSubject.onNext(checked);
    }

    public Observable<Boolean> getCameraProjectionObservable() {
        return cameraProjectionSubject;
    }
}
