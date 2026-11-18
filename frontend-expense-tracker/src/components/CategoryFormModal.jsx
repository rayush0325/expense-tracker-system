import { useContext, useState } from "react"
import { UserContext } from "../context/UserContext"
import "./CategoryFormModal.css"


export default function CategoryFormModal({
    savingCategory,
    handleCloseModal,
    handleSaveCategory,
    mode,
    category
}) {
    const { user } = useContext(UserContext);
    const [formData, setFormData] = useState({
        name: (category) ? category.name : "",
        global: false
    });

    const [error, setError] = useState(null);

    function handleChange(e) {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    }
    function handleSubmit(e) {
        e.preventDefault();
        const validationError = validateFormData();
        setError(validationError);
        if (validationError !== null) {
            return;
        }
        if (mode === 'ADD') {
            handleSaveCategory(formData)
        }
        else if (mode === 'UPDATE') {
            handleSaveCategory(formData, category.id)
        }

    }
    function validateFormData() {
        let validationError = null;
        const categoryName = formData.name;
        if (categoryName.trim().length === 0) {
            validationError = "category name cannot be blank";
        }
        else if (categoryName.trim().length > 50) {
            validationError = "category name must be within 50 characters";
        }
        return validationError;
    }
    return (
        <>
        <div className="modal-backdrop fade show"></div>
        <div className="modal show d-block " id="addCategoryModal" >
            <div className="modal-dialog modal-dialog-centered  category-modal-dialog">
                <div className="modal-content bg-dark  text-white border rounded-4 border-white shadow-lg ">
                    <div className="modal-header ">
                        <h5 className="modal-title">{mode === 'ADD' ? 'Add' : 'Update'}  category</h5>
                    <button type="button" className="btn-close" onClick={handleCloseModal} disabled={savingCategory}></button>
                    </div>

                    <div className="modal-body">
                        <form onSubmit={handleSubmit}>
                            <div>
                                <label htmlFor="categoryNameInput" className="form-label">Category Name</label>
                                <input
                                    id="categoryNameInput"
                                    onChange={handleChange}
                                    value={formData.name}
                                    name="name"
                                    type="text"
                                    className="form-control mb-3 "
                                    placeholder="Enter category name " />
                                {error && <div className="text-danger">{error}</div>}
                            </div>
                            {(user.role === 'ADMIN') && (mode === 'ADD') &&
                                <div>
                                    <div className="form-check mb-3">
                                        <input
                                            className="form-check-input"
                                            type="checkbox"
                                            name="global"
                                            id="global"
                                            onChange={(e) => {
                                                setFormData({
                                                    ...formData,
                                                    global: e.target.checked
                                                })
                                            }} />
                                        <label className="form-check-label" htmlFor="global">
                                            Make it a Global Category
                                        </label>
                                    </div>
                                </div>
                            }
                            <div className="d-flex justify-content-end gap-2">

                                <button type="button" className="btn btn-danger " onClick={handleCloseModal} disabled={savingCategory}>Cancel</button>
                                <button type="submit" className="btn btn-success" disabled={savingCategory}>{(savingCategory) ? "Saving..." : "Save Category"}</button>
                            </div>
                        </form>
                    </div>

                </div>
            </div>
        </div>
        </>
    )
}