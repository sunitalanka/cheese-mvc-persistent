package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menuList", menuDao.findAll());
        model.addAttribute("title", "Menu");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        model.addAttribute("menuList", menuDao.findAll());
        return "menu/add";
    }



    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu newMenu,
                                     Errors errors,  Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }
        //Cheese ch = cheeseDao.findOne(id);
        //newMenu.addItem(ch);
        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getMenuId();
    }


    @RequestMapping(path = "view/{menuID}",method = RequestMethod.GET)
    public String viewMenuForm(Model model,@PathVariable int menuID ){

        Menu m = menuDao.findOne(menuID);
        model.addAttribute("cheeses",m.getCheeses());
        model.addAttribute("title", m.getName());
        model.addAttribute("menuId", m.getMenuId());
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuID}", method = RequestMethod.GET)
    public String addItem(@PathVariable int menuID, Model model){

        Menu m = menuDao.findOne(menuID);

        AddMenuItemForm amif = new AddMenuItemForm (m, cheeseDao.findAll());
        model.addAttribute("form",amif);
        model.addAttribute("title", m.getName());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item/{menuID}", method = RequestMethod.POST)
    public String addItem(Model model,@ModelAttribute @Valid AddMenuItemForm aMenuForm, Errors errors,@PathVariable int menuID){

        if (errors.hasErrors()) {
            return "menu/add-item"+ menuID;
        }

        Menu theMenu = menuDao.findOne(menuID);
        Cheese c = cheeseDao.findOne(aMenuForm.getCheeseId());
        theMenu.addItem(c);

        menuDao.save(theMenu);

        return "redirect:/menu/view/" + theMenu.getMenuId();
    }



}
