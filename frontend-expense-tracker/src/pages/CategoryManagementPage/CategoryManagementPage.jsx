import { useEffect, useState } from "react"
import apiClient from "../../services/apiClient";
import { handleApiError } from "../../utils/handleApiError";
import CategoryItem from "../../components/CategoryItem";

export default function CategoryManagementPage() {

    const [categoryList, setCategoryList] = useState([]);

    useEffect(() => {
        getCategoryList();
    }, []);

    async function getCategoryList() {
        try {
            const response = await apiClient.get("/categories/");
            setCategoryList(response.data);
        } catch (error) {
            handleApiError(error);
        }
    }

    const demoCategoryList = [
        { id: 1, name: "travel", userId: 10 },
        { id: 2, name: "foods", userId: 20 },
        { id: 3, name: "study", userId: null },
    ];

    return (
        // <ul>
        //     {demoCategoryList.map((category) => {
        //         return <li>{category.name}</li>
        //     })}
        // </ul>
        <div className="min-vh-100 bg-dark text-white p-3">
            <div className="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center mb-4">
                <div>
                    <h1>Categories</h1>
                    <div>Manage your categories</div>
                </div>
                <button type="button " className="btn btn-primary border border-black align-self-end">add category</button>
            </div>
            <hr />
            <div className="p-3">
                <h3 className="mb-4">My Categories</h3>
                <div className="row">
                    {demoCategoryList
                        .filter((category) => { return category.userId !== null })
                        .map((category) => {
                            return <CategoryItem key={category.id} category={category} />
                        })}
                </div>
            </div>
            <hr />
            <div className="p-3">
                <h3 className="mb-4">Global Categories</h3>
                <div className="row">
                    {demoCategoryList
                        .filter((category) => { return category.userId === null })
                        .map((category) => {
                            return <CategoryItem key={category.id} category={category} />
                        })}
                </div>
            </div>
        </div>
    )
    
}

