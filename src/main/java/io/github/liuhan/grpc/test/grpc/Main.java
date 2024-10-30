/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.github.liuhan.grpc.test.grpc;

import io.github.liuhan.grpc.test.HelloServiceHandler;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        ExecutorService executor = new ThreadPoolExecutor(
            128, 128, 60, TimeUnit.SECONDS, new SynchronousQueue<>(),
            new DefaultThreadFactory("grpcServerPool"), new CustomRejectedExecutionHandler()
        );
        final Server server = ServerBuilder.forPort(8888)
            .addService(new HelloServiceHandler())
            .executor(executor)
            .build();

        server.start();
        new LinkedBlockingQueue<Boolean>().take();
    }

    static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.out.println("Task "+r.toString()+" rejected from " + executor.toString());
        }
    }

}
