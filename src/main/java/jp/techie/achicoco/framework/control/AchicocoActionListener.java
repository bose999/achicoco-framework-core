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

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jp.techie.achicoco.framework.conf.FrameworkConfing;
import jp.techie.achicoco.framework.util.LogUtil;

/**
 * 非同期処理 状態別処理クラス
 * 
 * @author bose999
 *
 */
public class AchicocoActionListener implements AsyncListener {

    /**
     * ログユーティリティ
     */
    private static LogUtil logUtil = new LogUtil(AchicocoActionListener.class);

    /**
     * 非同期処理 終了時処理
     * 
     * @param asyncEvent AsyncEvent
     */
    public void onComplete(AsyncEvent asyncEvent) throws IOException {
        logUtil.trace("onComplete");
    }

    /**
     * 非同期処理 エラー時処理
     * 
     * @param asyncEvent AsyncEvent
     */
    public void onError(AsyncEvent asyncEvent) throws IOException {
        try {
            logUtil.fatal("AchicocoActionListener onError");

            AsyncContext asyncContext = asyncEvent.getAsyncContext();
            ServletRequest req = asyncContext.getRequest();
            FrameworkConfing frameworkConfing = (FrameworkConfing) req.getAttribute("frameworkConfing");

            // When error occurred, we can't use AsyncContext's dispatch
            RequestDispatcher dispatch = req.getRequestDispatcher(frameworkConfing.getErrorUrl());
            ServletResponse res = asyncContext.getResponse();
            dispatch.forward(req, res);

            logUtil.fatal("dispatch errorPage");

        } catch (Exception e) {
            try {
                logUtil.fatal("AchicocoActionListener.onError() Exception", e);
                e.printStackTrace();
                throw e;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 非同期処理 開始時処理
     * 
     * @param asyncEvent AsyncEvent
     */
    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
        logUtil.trace("onStartAsync");
    }

    /**
     * 非同期処理 タイムアウト時処理
     * 
     * @param asyncEvent AsyncEvent
     */
    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
        logUtil.fatal("Service Timeout");
    }
}
