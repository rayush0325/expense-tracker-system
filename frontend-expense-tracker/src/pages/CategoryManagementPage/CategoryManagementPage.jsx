import { useEffect, useState } from "react"
import apiClient from "../../services/apiClient";
import { handleApiError } from "../../utils/handleApiError";
import CategoryItem from "../../components/CategoryItem";
import AddCategoryModal from "../../components/AddCategoryModal";

export default function CategoryManagementPage() {

    const [categoryList, setCategoryList] = useState([]);
    const [loading, setLoading] = useState(false);
    const [loadingCategories, setLoadingCategories] = useState(false);
    const [showModal, setShowModal] = useState(false);

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
            setLoading(true);
            await apiClient.post("/categories/", formData);
            closeModal();
            getCategoryList();
        } catch (error) {
            handleApiError(error);
        } finally {
            setLoading(false);
        }
    }

    function closeModal() {
        setShowModal(false);
    }

    return (
        <>
            {showModal && <AddCategoryModal handleAddCategory={addCategory} loading={loading} show={showModal} handleCloseModal={closeModal} />}

            <div className="min-vh-100 bg-dark text-white p-3">

                <div className="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center mb-4">
                    <div>
                        <h1>Categories</h1>
                        <div>Manage your categories</div>
                    </div>
                    <button type="button " onClick={() => setShowModal(true)} className="btn btn-primary btn-md  border border-black align-self-end text-white">Add category</button>
                </div>
                <hr />

                {loadingCategories
                    ? <h3>Loading Categories...</h3>
                    : <div>
                        <div className="p-3">
                            <h3 className="mb-4">Global Categories</h3>
                            <div className="row">
                                {categoryList
                                    .filter((category) => { return category.userId === null })
                                    .map((category) => {
                                        return <CategoryItem key={category.id} category={category} />
                                    })}
                            </div>
                        </div>
                        <hr />
                        <div className="p-3">
                            <h3 className="mb-4">My Categories</h3>
                            <div className="row">
                                {categoryList
                                    .filter((category) => { return category.userId !== null })
                                    .map((category) => {
                                        return <CategoryItem key={category.id} category={category} />
                                    })}
                            </div>
                        </div>
                    </div>
                }


            </div>
        </>
    )

}

