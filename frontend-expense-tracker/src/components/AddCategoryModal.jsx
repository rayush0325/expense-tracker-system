import { useContext, useState } from "react"
import { UserContext } from "../context/UserContext"
import "./AddCategoryModal.css"


export default function AddCategoryModal({ handleAddCategory, loading, show, handleCloseModal }) {
    const { user } = useContext(UserContext);
    const [formData, setFormData] = useState({
        name: "",
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
        const error = validateFormData();
        setError(error);
        if (error !== null) {
            return;
        }
        handleAddCategory(formData)
    }
    function validateFormData() {
        let error = null;
        const categoryName = formData.name;
        if (categoryName.trim().length === 0) {
            error = "category name cannot be blank";
        }
        else if (categoryName.trim().length > 50) {
            error = "category name must be within 50 characters";
        }
        return error;
    }
    if (!show) return null;
    return (
        <div className="modal show d-block " id="addCategoryModal " >
            <div className="modal-dialog modal-dialog-centered  category-modal-dialog">
                <div className="modal-content bg-dark  text-white border border-white shadow-lg ">
                    <div className="modal-header ">
                        <h5 className="modal-title">Add a category</h5>
                        <button type="button" className="btn-close" onClick={handleCloseModal} ></button>
                    </div>

                    <div className="modal-body">
                        <form onSubmit={handleSubmit}>
                            <div>
                                <label htmlFor="categoryNameInput" className="form-label">Category Name</label>
                                <input
                                    id="categoryNameInput"
                                    onChange={handleChange}
                                    name="name"
                                    type="text"
                                    className="form-control mb-3 "
                                    placeholder="Enter category name " />
                                {error && <div className="text-danger">{error}</div>}
                            </div>
                            {(user.role === 'ADMIN') &&
                                <div>
                                    <div className="form-check mb-3">
                                        <input
                                            className="form-check-input"
                                            type="checkbox"
                                            name="globalCategoryRadio"
                                            id="globalCategoryRadio"
                                            onChange={(e) => {
                                                setFormData({
                                                    ...formData,
                                                    global: e.target.checked
                                                })
                                            }} />
                                        <label className="form-check-label" htmlFor="globalCategoryRadio">
                                            Make it a Global Category
                                        </label>
                                    </div>
                                </div>
                            }
                            <div className="d-flex justify-content-end gap-2">
                                
                                <button type="button" className="btn btn-danger " onClick={handleCloseModal}>Cancel</button>
                                <button type="submit" className="btn btn-success">{(loading) ? "Saving..." : "Save Category"}</button>
                            </div>
                        </form>
                    </div>

                </div>
            </div>
        </div>
    )
}