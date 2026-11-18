import { useEffect, useState } from "react"
import apiClient from "../../services/apiClient";
import { handleApiError } from "../../utils/handleApiError";
import CategoryItem from "../../components/CategoryItem";
import CategoryFormModal from "../../components/CategoryFormModal";
import ConfirmDeleteModal from "../../components/ConfirmDeleteModal";

export default function CategoryManagementPage() {

    const [categoryList, setCategoryList] = useState([]);
    const [savingCategory, setSavingCategory] = useState(false);
    const [loadingCategories, setLoadingCategories] = useState(false);
    const [deletingCategory, setDeletingCategory] = useState(false);
    const [category, setCategory] = useState(null);
    const [modalMode, setModalMode] = useState(null);

    const personalCategoryList = categoryList.filter(category => category.userId !== null);
    const globalCategoryList = categoryList.filter(category => category.userId === null);

    useEffect(() => {
        getCategoryList();
    }, []);

    async function getCategoryList() {
        try {
            setLoadingCategories(true);
            const response = await apiClient.get("/categories/");
            setCategoryList(response.data);
        } catch (error) {
            handleApiError(error);
        } finally {
            setLoadingCategories(false);
        }
    }

    async function addCategory(formData) {

        try {
            setSavingCategory(true);
            await apiClient.post("/categories/", formData);
            closeModal();
            getCategoryList();
        } catch (error) {
            handleApiError(error);
        } finally {
            setSavingCategory(false);
        }
    }

    async function updateCategory(formData, categoryId) {
        try {
            setSavingCategory(true);
            await apiClient.put(`/categories/${categoryId}`, formData);
            closeModal();
            getCategoryList();
        } catch (error) {
            handleApiError(error);
        } finally {
            setSavingCategory(false);
        }
    }

    async function deleteCategory(categoryId) {
        try {
            setDeletingCategory(true);
            await apiClient.delete(`/categories/${categoryId}`);
            closeModal();
            getCategoryList();
        } catch (error) {
            handleApiError(error);
        } finally {
            setDeletingCategory(false);
        }
    }

    function handleCategoryUpdate(category) {
        setCategory(category);
        setModalMode('UPDATE');
    }

    function handleAddCategory() {
        setModalMode('ADD');
    }

    function handleCategoryDelete(category) {
        setCategory(category);
        setModalMode("DELETE");
    }


    function closeModal() {
        setModalMode(null);
        setCategory(null);
    }

    return (
        <>
            {modalMode === 'ADD' && <CategoryFormModal savingCategory={savingCategory} mode={modalMode} handleCloseModal={closeModal} handleSaveCategory={addCategory} />}
            {modalMode === 'UPDATE' && <CategoryFormModal savingCategory={savingCategory} mode={modalMode} handleCloseModal={closeModal} handleSaveCategory={updateCategory} category={category} />}
            {modalMode === 'DELETE' && <ConfirmDeleteModal deleting={deletingCategory} handleCancel={closeModal} handleConfirmDelete={deleteCategory} id={category.id} />}
            <div className="min-vh-100 bg-dark text-white p-3">

                <div className="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center mb-4">
                    <div>
                        <h1>Categories</h1>
                        <div>Manage your categories</div>
                    </div>
                    <button type="button " onClick={handleAddCategory} className="btn btn-primary btn-md  border border-black align-self-end text-white">Add category</button>
                </div>
                <hr />

                {loadingCategories
                    ? <h3>Loading Categories...</h3>
                    : <div>
                        <div className="p-3">
                            <h3 className="mb-4">Global Categories</h3>
                            <div className="row">
                                {globalCategoryList.length === 0
                                    ? <p className="fs-5">No global categories available.</p>
                                    : globalCategoryList.map((category) => {
                                        return <CategoryItem key={category.id} category={category} />
                                    })}
                            </div>
                        </div>
                        <hr />
                        <div className="p-3">
                            <h3 className="mb-4">My Categories</h3>
                            <div className="row">
                                {personalCategoryList.length === 0
                                    ? <p className="fs-5">No personal categories yet.</p>
                                    : personalCategoryList.map((category) => {
                                        return <CategoryItem key={category.id} category={category} handleCategoryUpdate={handleCategoryUpdate} handleCategoryDelete={handleCategoryDelete} />
                                    })}
                            </div>
                        </div>
                    </div>
                }
            </div>
        </>
    )

}

