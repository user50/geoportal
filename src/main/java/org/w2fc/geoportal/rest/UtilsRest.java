package org.w2fc.geoportal.rest;


import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w2fc.geoportal.dao.UtilsDao;
import org.w2fc.geoportal.domain.AbstractDomain;


@Controller
@RequestMapping(value = "/utils")

public class UtilsRest {

    final Logger logger = LoggerFactory.getLogger(UtilsRest.class);

    @Autowired
    private UtilsDao utilsDao;


    @Transactional(readOnly = true)
    @RequestMapping(value = "/hql")
    public String exec1(@RequestParam(required = false)
    String query, Model model) throws Throwable {
        List<Object> list = null;

        model.addAttribute("query", query);

        try {
            if (null != query)
                list = utilsDao.exec(query);
            model.addAttribute("result", list);
        } catch (Throwable t) {
            logger.error("------------------------------------------------------------------------------------------------", t);
            logger.error("------------------------------------------------------------------------------------------------");
            model.addAttribute("_exception", t.getMessage());
        }
        return "utils/hql";
    }


    @Transactional(value = "transactionManagerCartography", readOnly = true)
    @RequestMapping(value = "/c/hql")
    public String exec2(@RequestParam(required = false) String query, Model model) throws Throwable {
        List<Object> list = null;

        model.addAttribute("query", query);

        try {
            if (null != query)
                list = utilsDao.exec(query, UtilsDao.DataSources.CARTOGRAPHY);
            model.addAttribute("result", list);
        } catch (Throwable t) {
            logger.error("------------------------------------------------------------------------------------------------", t);
            logger.error("------------------------------------------------------------------------------------------------");
            model.addAttribute("_exception", t.getMessage());
        }
        return "utils/hql";
    }


    @Transactional(readOnly=true)
    @RequestMapping(value="/hql/{q}")
    public @ResponseBody List<Object> exec(@PathVariable("q") String query) {
        List<Object> list = utilsDao.exec(query);
        List<Object> res = new LinkedList<Object>();

        try {
            for (Object o : list) {
                res.add(((AbstractDomain)o).clone());
            }            
        } catch (ClassCastException e) {
            return list;
        }

        return res;
    }


    @RequestMapping(value="/sql/{ds}/{q}")
    public @ResponseBody List execSql(@PathVariable("q") String query, @PathVariable("ds") String ds) {
        return utilsDao.execSql(query, ds.equalsIgnoreCase("p") ? UtilsDao.DataSources.PORTAL : UtilsDao.DataSources.CARTOGRAPHY);
    }
}
