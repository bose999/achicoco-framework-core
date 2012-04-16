/*
 * Copyright 2012 bose999.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.techie.achicoco.framework.control;

import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.techie.achicoco.framework.action.AchicocoAction;
import jp.techie.achicoco.framework.conf.FrameworkConfing;
import jp.techie.achicoco.framework.util.LogUtil;

/**
 * サーブレットから非同期で処理を受け取り<br />
 * URLから判別してActionを実行するクラス
 * 
 * @author bose999
 *
 */
public abstract class AchicocoActionControler implements Runnable {

    /**
     * ログユーティリティ
     */
    public static LogUtil logUtil = new LogUtil(AchicocoActionControler.class);

    /**
     * AsyncContext
     */
    public AsyncContext asyncContext;

    /**
     * ServletContext
     */
    public ServletContext application;

    /**
     * HttpServletRequest
     */
    public HttpServletRequest request;

    /**
     * HttpServletResponse
     */
    public HttpServletResponse response;

    /**
     * HttpSession
     */
    public HttpSession session;

    /**
     * URLから生成するActionクラス名
     */
    public String actionClassName;

    /**
     * URLパラメータ格納List
     */
    public List<String> urlParamList;

    /**
     * サーブレットから実行されるメソッド
     * ここでActionを生成し処理を実行する。
     */
    public void run() {
        try {
            long startTime = 0;

            if (logUtil.isTraceEnabled()) {
                // 処理開始ログ出力
                startTime = System.currentTimeMillis();
                logUtil.trace("Start AchicocoActionControler run Method");
            }

            AchicocoAction achicocoAction = getAchicocoActionInstance();

            if (achicocoAction != null) {
                // 共通実行メソッドを実行し処理を行う
                achicocoAction.run();

                // 処理終了後 遷移先を設定
                logUtil.trace("dipatchUrl:" + achicocoAction.dispatchUrl);
                asyncContext.dispatch(achicocoAction.dispatchUrl);
            }

            if (logUtil.isTraceEnabled()) {
                // 処理時間ログ出力
                long doTime = System.currentTimeMillis() - startTime;
                logUtil.trace("AchicocoActionControler run Method:" + doTime + "ms.");
            }
        } catch (Exception e) {
            logUtil.fatal("AchicocoActionControler exception", e);
            try {
                throw e;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * FrameworkConfing取得メソッド
     * 
     * @return FrameworkConfing
     */
    protected abstract FrameworkConfing getFrameworkConfing() throws Exception;

    /**
     * FrameworkConfingインスタンス化処理
     * getFrameworkConfingメソッド実行時の例外を処理
     * 
     * @return FrameworkConfing
     * @throws Exception
     */
    protected FrameworkConfing getFrameworkConfingInstance() throws Exception {
        FrameworkConfing frameworkConfing = null;
        try {
            frameworkConfing = getFrameworkConfing();
            // AchicocoActionListenerでのエラー処理時の為にリクエストに格納しておく
            request.setAttribute("frameworkConfing", frameworkConfing);
        } catch (ClassNotFoundException ce) {
            // 設定クラスが見つからない場合
            logUtil.fatal("AchicocoActionControler can't get FrameworkConfing", ce);
            try {
                throw ce;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            // 設定クラス取得で問題があった場合
            logUtil.fatal("AchicocoActionControler can't get FrameworkConfing", e);
            try {
                throw e;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return frameworkConfing;
    }

    /**
     * AchicocoAction取得メソッド
     * 
     * @param actionClassName アクションクラス名
     * @return AchicocoAction
     * @throws ClassNotFoundException
     */
    protected abstract AchicocoAction getAchicocoAction(String actionClassName) throws Exception;

    /**
     * AchicocoActionインスタンス化処理
     * getAchicocoActionメソッド実行時の例外を処理
     * 
     * @return AchicocoAction
     * @throws Exception
     */
    protected AchicocoAction getAchicocoActionInstance() throws Exception {
        FrameworkConfing frameworkConfing = null;
        AchicocoAction achicocoAction = null;
        try {

            frameworkConfing = getFrameworkConfingInstance();

            // URLからActionクラスを特定してインスタンス化 インスタンス化の手段はgetAchicocoActionで定義
            achicocoAction = getAchicocoAction(actionClassName);

            // インスタンス化したActionに値をセット ActionはSingletonだとThread Safeにならないので注意
            achicocoAction.application = application;
            achicocoAction.request = request;
            achicocoAction.response = response;
            achicocoAction.session = session;
            achicocoAction.urlParamList = urlParamList;

        } catch (ClassNotFoundException ce) {
            // URLが間違っていた等でクラスが見つからない場合ログを出力してエラー画面へ遷移
            logUtil.debug("AchicocoActionControler errorUrl open");
            request.setAttribute("errorString", frameworkConfing.getMessageBadUrl());
            asyncContext.dispatch(frameworkConfing.getErrorUrl());
            try {
                throw ce;
            } catch (Exception e) {
            }
        }
        return achicocoAction;
    }
}
