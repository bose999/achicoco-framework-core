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
package jp.techie.achicoco.framework.conf;

/**
 * フレームワークの設定
 * 
 * @author bose999
 *
 */
public interface FrameworkConfing {

    /**
     * AchicocoSettingインスタンス化
     * 
     * @return AchicocoSetting
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public ApplicationConfing getApplicationConfing() throws ClassNotFoundException, InstantiationException,
            IllegalAccessException;

    /**
     * アクションクラスのパッケージ名を返す
     * 
     * @return アクションクラスのパッケージ名
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public String getActionPackageName() throws ClassNotFoundException, InstantiationException,
            IllegalAccessException;

    /**
     * エラー時のURLを返す
     * 
     * @return エラー時のURL
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public String getErrorUrl() throws ClassNotFoundException, InstantiationException, IllegalAccessException;

    /**
     * URLが不正の時のエラーメッセージを返す
     * 
     * @return URLが不正の時のエラーメッセージを返す
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public String getMessageBadUrl() throws ClassNotFoundException, InstantiationException, IllegalAccessException;
}
