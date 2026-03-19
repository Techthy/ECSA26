package tools.vitruv.methodologisttemplate.vsum;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import mir.reactions.adr2adr.Adr2adrChangePropagationSpecification;
import mir.reactions.adr2ticket.Adr2ticketChangePropagationSpecification;
import mir.reactions.c22pcm.C22pcmChangePropagationSpecification;
import mir.reactions.uncertainty2uncertainty.Uncertainty2uncertaintyChangePropagationSpecification;
import tools.vitruv.change.propagation.ChangePropagationMode;
import tools.vitruv.change.testutils.TestUserInteraction;
import tools.vitruv.framework.views.CommittableView;
import tools.vitruv.framework.views.View;
import tools.vitruv.framework.views.ViewTypeFactory;
import tools.vitruv.framework.vsum.VirtualModel;
import tools.vitruv.framework.vsum.VirtualModelBuilder;
import tools.vitruv.framework.vsum.internal.InternalVirtualModel;

public class TestUtil {

    /**
     * Loads a resource from the given absolute path starting at the project root
     * using the provided ResourceSet.
     * 
     * @param resourceSet The ResourceSet to use for loading the resource.
     * @param pathName    The file path of the resource to load.
     */
    public static Resource loadResource(ResourceSet resourceSet, String pathName) {

        Resource resource = resourceSet
                .createResource(URI.createFileURI(pathName));
        try {
            resource.load(Collections.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        EcoreUtil.resolveAll(resource);

        return resource;

    }

    /**
     * Registers a root object in the virtual model at the specified file path.
     * 
     * @param virtualModel The virtual model in which to register the root object.
     * @param filePath     The file path where the root object will be stored.
     * @param rootObject   The Object that is to be registered as root.
     * @param rootTypes    The types of root objects to be considered.
     *                     Note: the root types could be multiple if the model
     *                     contains references to other root objects of different
     *                     types.
     */
    public static void registerRootObject(VirtualModel virtualModel, Path filePath, EObject rootObject,
            List<Class<?>> rootTypes) {
        CommittableView view = getDefaultView(virtualModel,
                rootTypes)
                .withChangeDerivingTrait();
        modifyView(view, (CommittableView v) -> {
            v.registerRoot(
                    rootObject,
                    org.eclipse.emf.common.util.URI
                            .createFileURI(
                                    filePath.toString() + "/" + rootObject.getClass().getSimpleName() + ".model"));
        });
    }

    public static InternalVirtualModel createDefaultVirtualModel(Path projectPath) {
        InternalVirtualModel model = new VirtualModelBuilder()
                .withStorageFolder(projectPath)
                .withUserInteractorForResultProvider(
                        new TestUserInteraction.ResultProvider(new TestUserInteraction()))
                .withChangePropagationSpecification(
                        new Uncertainty2uncertaintyChangePropagationSpecification())
                .withChangePropagationSpecification(new C22pcmChangePropagationSpecification())
                .withChangePropagationSpecification(new Adr2ticketChangePropagationSpecification())
                .withChangePropagationSpecification(new Adr2adrChangePropagationSpecification())
                .buildAndInitialize();
        model.setChangePropagationMode(ChangePropagationMode.TRANSITIVE_CYCLIC);
        return model;
    }

    // See https://github.com/vitruv-tools/Vitruv/issues/717 for more information
    // about the rootTypes
    public static View getDefaultView(VirtualModel vsum, Collection<Class<?>> rootTypes) {
        var selector = vsum.createSelector(ViewTypeFactory.createIdentityMappingViewType("default"));
        selector.getSelectableElements().stream()
                .filter(element -> rootTypes.stream().anyMatch(it -> it.isInstance(element)))
                .forEach(it -> selector.setSelected(it, true));
        return selector.createView();
    }

    private static void modifyView(CommittableView view, Consumer<CommittableView> modificationFunction) {
        modificationFunction.accept(view);
        view.commitChanges();
    }

}
