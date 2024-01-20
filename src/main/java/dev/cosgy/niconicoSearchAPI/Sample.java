/*
 *  Copyright 2024 Cosgy Dev (info@cosgy.dev).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package dev.cosgy.niconicoSearchAPI;

import java.util.List;

public class Sample {
    public static void main(String[] args) {
        nicoSearchAPI ns = new nicoSearchAPI(true, 5);

        long start = System.currentTimeMillis();
        List<nicoVideoSearchResult> results_0 = ns.searchVideo("初音ミク", 5);
        long end = System.currentTimeMillis();

        long start_2 = System.currentTimeMillis();
        List<nicoVideoSearchResult> results_1 = ns.searchVideo("千本桜", 5);
        long end_2 = System.currentTimeMillis();

        long start_3 = System.currentTimeMillis();
        List<nicoVideoSearchResult> results_2 = ns.searchVideo("ODDS&ENDS", 5);
        long end_3 = System.currentTimeMillis();

        System.out.println("First: " + (end - start));
        results_0.forEach(result -> System.out.println(result.getTitle() + ": " + result.getWatchUrl()));

        System.out.println("Second: " + (end_2 - start_2));
        results_1.forEach(result -> System.out.println(result.getTitle() + ": " + result.getWatchUrl()));

        System.out.println("Third: " + (end_3 - start_3));
        results_2.forEach(result -> System.out.println(result.getTitle() + ": " + result.getWatchUrl()));
    }
}
