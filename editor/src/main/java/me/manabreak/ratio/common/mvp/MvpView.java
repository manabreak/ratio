package me.manabreak.ratio.common.mvp;

import me.manabreak.ratio.ui.DefTable;

public abstract class MvpView<P extends MvpPresenter> extends DefTable {

    protected final P presenter;

    protected MvpView(P presenter) {
        super();
        this.presenter = presenter;
        presenter.attachView(this);
    }

    public P getPresenter() {
        return presenter;
    }
}
