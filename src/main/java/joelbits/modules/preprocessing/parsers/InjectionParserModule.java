package joelbits.modules.preprocessing.parsers;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import joelbits.modules.preprocessing.parsers.utils.ASTNodeCreator;

public class InjectionParserModule extends AbstractModule {
    @Override
    protected void configure() { }

    @Provides
    public ASTNodeCreator getASTNodeCreator() { return new ASTNodeCreator(); }
}
