package jpatest.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import CodeCompile.RuntimeCompiler;
import jpatest.ExamSubject.dao.ExamSubject;
import jpatest.ExamSubject.dao.ExamSubjectRepository;

@Controller

public class ExamSubjectController {

	  @GetMapping("/greeting")
	    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
	        model.addAttribute("name", name);
	        return "greeting";
	  }

	@GetMapping("/subject")
	    public String queryAll(Model model){
	        List<ExamSubject> list = new ArrayList<ExamSubject>();
	        list = examSubjectRepository.findAll();
	        Random random = new Random();
	        System.out.println("list size is "+list.size());
	        int i=random.nextInt(list.size());
	        
	        System.out.println(i);
	        model.addAttribute("subject", (ExamSubject)list.get(i));
	        
	        return "subject";
	    }
	    @Autowired
	    private ExamSubjectRepository examSubjectRepository;
	    
	    
	    @PostMapping("/code")
	    public String codeExecute(@RequestParam(name="code") String code, Model model) {
	    	
	    	RuntimeCompiler rc=new RuntimeCompiler();
	    	
	    	String sout=rc.executecode(code);
	    	model.addAttribute("sout", sout);
	    	return "codeResult";
	    }
}
