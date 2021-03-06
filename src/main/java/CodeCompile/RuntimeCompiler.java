package CodeCompile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

public class RuntimeCompiler {
	
	public  String executecode(String code){
	  String program = code;

		    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		    String classname=code.substring(code.indexOf("class")+5, code.indexOf("{")).trim();
		    System.out.println("classname is "+classname);
		    JavaFileObject compilationUnit =
		        new StringJavaFileObject(classname, program);

		    SimpleJavaFileManager fileManager =
		        new SimpleJavaFileManager(compiler.getStandardFileManager(null, null, null));

		    JavaCompiler.CompilationTask compilationTask = compiler.getTask(
		        null, fileManager, null, null, null, Arrays.asList(compilationUnit));

		    compilationTask.call();

		    CompiledClassLoader classLoader =
		        new CompiledClassLoader(fileManager.getGeneratedOutputFiles());

		    
		 // Create a stream to hold the output
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    PrintStream printStream = new PrintStream(baos);
		    // Tell Java to use your special stream
		    System.setOut(printStream);
		    
		    Class<?> autoClass;
			try {
				autoClass = classLoader.loadClass(classname);
		
		    Method main = autoClass.getMethod("main", String[].class);
		    main.invoke(null, new Object[]{null});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// flush output stream 
			System.out.flush();
			
			return baos.toString();
		  }

		  private static class StringJavaFileObject extends SimpleJavaFileObject {
		    private final String code;

		    public StringJavaFileObject(String name, String code) {
		      super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
		          Kind.SOURCE);
		      this.code = code;
		    }

		    @Override
		    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		      return code;
		    }
		  }

		  private static class ClassJavaFileObject extends SimpleJavaFileObject {
		    private final ByteArrayOutputStream outputStream;
		    private final String className;

		    protected ClassJavaFileObject(String className, Kind kind) {
		      super(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind);
		      this.className = className;
		      outputStream = new ByteArrayOutputStream();
		    }

		    @Override
		    public OutputStream openOutputStream() throws IOException {
		      return outputStream;
		    }

		    public byte[] getBytes() {
		      return outputStream.toByteArray();
		    }

		    public String getClassName() {
		      return className;
		    }
		  }

		  private static class SimpleJavaFileManager extends ForwardingJavaFileManager {
		    private final List<ClassJavaFileObject> outputFiles;

		    protected SimpleJavaFileManager(JavaFileManager fileManager) {
		      super(fileManager);
		      outputFiles = new ArrayList<ClassJavaFileObject>();
		    }

		    @Override
		    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
		      ClassJavaFileObject file = new ClassJavaFileObject(className, kind);
		      outputFiles.add(file);
		      return file;
		    }

		    public List<ClassJavaFileObject> getGeneratedOutputFiles() {
		      return outputFiles;
		    }
		  }

		  private static class CompiledClassLoader extends ClassLoader {
		    private final List<ClassJavaFileObject> files;

		    private CompiledClassLoader(List<ClassJavaFileObject> files) {
		      this.files = files;
		    }

		    @Override
		    protected Class<?> findClass(String name) throws ClassNotFoundException {
		      Iterator<ClassJavaFileObject> itr = files.iterator();
		      while (itr.hasNext()) {
		        ClassJavaFileObject file = itr.next();
		        if (file.getClassName().equals(name)) {
		          itr.remove();
		          byte[] bytes = file.getBytes();
		          return super.defineClass(name, bytes, 0, bytes.length);
		        }
		      }
		      return super.findClass(name);
		    }
		  }
}