/*
* 
*/
package crossflow.diagram.edit.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;

import crossflow.diagram.edit.commands.DataField3CreateCommand;
import crossflow.diagram.edit.commands.EnumField3CreateCommand;
import crossflow.diagram.providers.CrossflowElementTypes;

/**
 * @generated
 */
public class TypeTypeFieldsCompartmentItemSemanticEditPolicy extends CrossflowBaseItemSemanticEditPolicy {

	/**
	* @generated
	*/
	public TypeTypeFieldsCompartmentItemSemanticEditPolicy() {
		super(CrossflowElementTypes.Type_2013);
	}

	/**
	* @generated
	*/
	protected Command getCreateCommand(CreateElementRequest req) {
		if (CrossflowElementTypes.DataField_3003 == req.getElementType()) {
			return getGEFWrapper(new DataField3CreateCommand(req));
		}
		if (CrossflowElementTypes.EnumField_3004 == req.getElementType()) {
			return getGEFWrapper(new EnumField3CreateCommand(req));
		}
		return super.getCreateCommand(req);
	}

}
