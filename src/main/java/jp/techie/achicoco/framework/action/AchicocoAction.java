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
package jp.techie.achicoco.framework.action;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * AchicocoフレームワークAction abstract クラス<br />
 * 自動でActionに割り当てる為のフィールドとInjection後の実行用のrun()を定義
 * 
 * @author bose999
 * 
 */
public abstract class AchicocoAction {

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
     * URLから引数を解析しListに格納
     */
    public List<String> urlParamList;

    /**
     * Action終了後の遷移先
     */
    public String dispatchUrl;

    /**
     * コントローラから実行されるメソッド
     */
    public abstract void run();
}
