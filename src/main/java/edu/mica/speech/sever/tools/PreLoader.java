package edu.mica.speech.sever.tools;

import java.io.IOException;

import edu.cmu.sphinx.frontend.BaseDataProcessor;
import edu.cmu.sphinx.frontend.Data;
import edu.cmu.sphinx.frontend.DataProcessingException;
import edu.cmu.sphinx.frontend.DataProcessor;
import edu.cmu.sphinx.linguist.acoustic.tiedstate.Loader;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4Component;

public class PreLoader extends BaseDataProcessor {
	@S4Component(type = Loader.class)
    public final static String PROP_LOADER = "loader";
    protected Loader loader;
    
    public PreLoader(Loader loader) throws IOException {
        initLogger();
        this.loader = loader;
        loader.load();
        //initDataProcessors();
    }
    public PreLoader() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public Data getData() throws DataProcessingException {
		// TODO Auto-generated method stub
		Data data = this.getPredecessor().getData();
		return data;
	}
	 @Override
	public void newProperties(PropertySheet ps) throws PropertyException {
	    super.newProperties(ps);
	    loader = (Loader) ps.getComponent(PROP_LOADER);
	    try {
	       loader.load();
	    } catch (IOException e) {
	            throw new PropertyException(e);
	    }
	}
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		super.initialize();
	}
	@Override
	public DataProcessor getPredecessor() {
		// TODO Auto-generated method stub
		return super.getPredecessor();
	}
	@Override
	public void setPredecessor(DataProcessor predecessor) {
		// TODO Auto-generated method stub
		super.setPredecessor(predecessor);
	}
	

}
