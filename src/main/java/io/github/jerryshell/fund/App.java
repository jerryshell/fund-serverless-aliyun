package io.github.jerryshell.fund;

import cn.hutool.core.util.StrUtil;
import com.aliyun.fc.runtime.Context;
import com.aliyun.fc.runtime.FunctionComputeLogger;
import com.aliyun.fc.runtime.FunctionInitializer;
import com.aliyun.fc.runtime.HttpRequestHandler;
import io.github.jerryshell.fund.service.FundService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

public class App implements HttpRequestHandler, FunctionInitializer {
    private FundService fundService;
    private FunctionComputeLogger logger;

    public void initialize(Context context) {
        logger = context.getLogger();
        fundService = new FundService();
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context context) throws IOException {
        String requestPath = (String) request.getAttribute("FC_REQUEST_PATH");
        logger.info(StrUtil.format("requestPath {}", requestPath));
        String requestURI = (String) request.getAttribute("FC_REQUEST_URI");
        logger.info(StrUtil.format("requestURI {}", requestURI));
        String requestClientIP = (String) request.getAttribute("FC_REQUEST_CLIENT_IP");
        logger.info(StrUtil.format("requestClientIP {}", requestClientIP));

        String fundCode = request.getParameter("fundCode");
        logger.info(StrUtil.format("fundCode {}", fundCode));

        BigDecimal jerryIndex = fundService.getJerryIndexByFundCode(fundCode);
        logger.info(StrUtil.format("jerryIndex {}", jerryIndex));

        response.setStatus(200);
        response.setHeader("content-type", "application/json");

        OutputStream out = response.getOutputStream();
        out.write(jerryIndex.toString().getBytes());
        out.flush();
        out.close();
    }
}
