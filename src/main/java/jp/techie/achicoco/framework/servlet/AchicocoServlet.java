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
package jp.techie.achicoco.framework.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jp.techie.achicoco.framework.control.AchicocoActionControler;
import jp.techie.achicoco.framework.control.AchicocoActionListener;
import jp.techie.achicoco.framework.util.LogUtil;

/**
 * AchicocoGuice連携サーブレット<br />
 * <br />
 * 非同期処理サーブレットなのでweb.xmlでの設定に注意<br />
 * アプリケーション要件によってサーブレット名を変えられるように<br />
 * アノテーションでは定義しない
 * 
 * @author bose999
 *
 */
@SuppressWarnings("serial")
public abstract class AchicocoServlet extends HttpServlet {

    /**
     * ログユーティリティ
     */
    public static LogUtil logUtil = new LogUtil(AchicocoServlet.class);

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        run(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        run(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) {
        run(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) {
        run(request, response);
    }

    /**
     * GET/POST/PUT/DELETEメソッドから呼び出される共通処理
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    protected void run(HttpServletRequest request, HttpServletResponse response) {
        try {
            long startTime = 0;
            if (logUtil.isTraceEnabled()) {
                startTime = System.currentTimeMillis();
                logUtil.trace("Start doGet Method");
            }

            // 非同期処理実行
            startAsync(request, response);

            if (logUtil.isTraceEnabled()) {
                long doTime = System.currentTimeMillis() - startTime;
                logUtil.trace("End doGet Method:" + doTime + "ms.");
            }
        } catch (Exception e) {
            try {
                logUtil.fatal("AchicocoCoreServlet.doGet() Exception", e);
                e.printStackTrace();
                throw e;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 非同期処理実行メッソド
     * 必要な値をセットして共通のコントローラを呼び出す
     * 
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    protected void startAsync(HttpServletRequest request, HttpServletResponse response) {
        AsyncContext asyncContext = request.startAsync();
        AchicocoActionListener achicocoActionListener = new AchicocoActionListener();
        asyncContext.addListener(achicocoActionListener);

        AchicocoActionControler achicocoActionControler = getAchicocoActionControler();
        achicocoActionControler.asyncContext = asyncContext;
        achicocoActionControler.actionClassName = makeClassName(request);
        achicocoActionControler.urlParamList = makeUrlParamList(request);
        achicocoActionControler.application = getServletContext();
        achicocoActionControler.request = request;
        achicocoActionControler.response = response;
        achicocoActionControler.session = request.getSession(true);

        logUtil.trace("Start AchicocoActionControler run async processing");
        asyncContext.start(achicocoActionControler);
    }

    /**
     * 
     * 各DIコンテナ毎でAchicocoActionCoreControlerを返す実装を行う
     * 
     * @return AchicocoActionCoreControler
     */
    protected abstract AchicocoActionControler getAchicocoActionControler();

    /**
     * URLからクラス名を生成
     * 
     * @param request HttpServletRequest
     * @return クラス名
     */
    protected String makeClassName(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String urlParamString = requestURI.substring(contextPath.length(), requestURI.length());
        String[] paramStrings = urlParamString.split("/", 0);
        String actionClassName = null;
        if (paramStrings.length >= 3) {
            // 0:emptyString 1:action 2:componet name after: param
            String actionName = paramStrings[2];
            logUtil.trace("actionName:" + actionName);
            actionClassName = actionName.substring(0, 1).toUpperCase() + actionName.substring(1);
        } else {
            logUtil.fatal("actionClassName:null Can't use application.");
        }

        return actionClassName;
    }

    /**
     * URLのメソッド移行のパラメータを/区切りからListに変換
     * 
     * @param request HttpServletRequest
     * @return パラメータ格納リスト
     */
    protected List<String> makeUrlParamList(HttpServletRequest request) {

        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String urlParamString = requestURI.substring(contextPath.length(), requestURI.length());
        String[] paramStrings = urlParamString.split("/", 0);
        int paramStringsLength = paramStrings.length;
        int nowLength = 0;
        List<String> paramValue = new ArrayList<String>();
        while (paramStringsLength > nowLength) {
            if (nowLength > 2) {
                // Contextとクラス名を除いてから格納
                paramValue.add(paramStrings[nowLength]);
            }
            nowLength++;
        }
        return paramValue;
    }
}
