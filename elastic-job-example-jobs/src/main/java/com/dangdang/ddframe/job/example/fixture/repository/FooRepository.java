/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.job.example.fixture.repository;

import com.dangdang.ddframe.job.example.fixture.entity.Foo;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class FooRepository {
    
    private Map<Long, Foo> data = new ConcurrentHashMap<>(300, 1);
    
    public FooRepository() {
        init();
    }
    
    private void init() {
        addData(0L, 10L, "Beijing");
        addData(10L, 20L, "Shanghai");
        addData(20L, 30L, "Guangzhou");
    }
    
    private void addData(final long idFrom, final long idTo, final String location) {
        for (long i = idFrom; i < idTo; i++) {
            data.put(i, new Foo(i, location, Foo.Status.TODO));
        }
    }
    
    public List<Foo> findTodoData(final String location, final int limit) {
        System.out.println("ShardingParameter,"+ location);
        List<Foo> result = new ArrayList<>(limit);
        int count = 0;
        for (Map.Entry<Long, Foo> each : data.entrySet()) {
            Foo foo = each.getValue();
            if (foo.getLocation().equals(location) && foo.getStatus() == Foo.Status.TODO) {
                result.add(foo);
                count++;
                if (count == limit) {
                    System.out.println("count==limit,"+ (count == limit));
                    break;
                }
            }
        }
        System.out.println("count,"+ count);
        return result;
    }
    
    public void setCompleted(final long id) {
        System.out.println(String.format("Time: %s | Thread: %s | %s", new SimpleDateFormat("HH:mm:ss").format(new Date()), Thread.currentThread().getId(), "DATAFLOW PROCESS.setCompleted"));
        data.get(id).setStatus(Foo.Status.COMPLETED);
    }
}
