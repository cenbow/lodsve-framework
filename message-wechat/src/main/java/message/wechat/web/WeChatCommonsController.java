package message.wechat.web;

import java.io.IOException;
import java.io.PrintWriter;
import message.base.utils.StringUtils;
import message.base.utils.XmlUtils;
import message.mvc.annotation.WebResource;
import message.mvc.commons.WebInput;
import message.mvc.commons.WebOutput;
import message.wechat.base.WeChatCommonsService;
import message.wechat.beans.JsApiConfig;
import message.wechat.beans.message.reply.Reply;
import message.wechat.message.ReceiveHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 16/2/21 下午5:13
 */
@RestController
@RequestMapping("/wx")
public class WeChatCommonsController {
    @Autowired
    private WeChatCommonsService weChatCommonsService;
    @Autowired
    private ReceiveHandler receiveHandler;

    @RequestMapping(value = "/message", produces = "application/json", method = RequestMethod.GET)
    public String check(String signature, String timestamp, String nonce, String echostr, @WebResource WebOutput out) throws IOException {
        Assert.notNull(signature);
        Assert.notNull(timestamp);
        Assert.notNull(nonce);
        Assert.notNull(echostr);

        boolean result = weChatCommonsService.checkSignature(timestamp, nonce, signature);
        PrintWriter writer = out.getResponse().getWriter();
        if (result) {
            writer.print(echostr);
        } else {
            writer.print(StringUtils.EMPTY);
        }

        writer.flush();
        writer.close();

        return StringUtils.EMPTY;
    }

    @RequestMapping(value = "/message", produces = "application/json", method = RequestMethod.POST)
    public String handleMessage(@WebResource WebInput in) {
        try {
            Reply reply = receiveHandler.handle(XmlUtils.parse(in.getRequest().getInputStream()));
            return XmlUtils.toXML(reply);
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    @RequestMapping(value = "/js/config", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public JsApiConfig jsApiConfig(String url) {
        return weChatCommonsService.jsApiConfig(url);
    }
}
