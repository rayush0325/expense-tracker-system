import { useEffect, useState } from "react"
import apiClient from "../services/apiClient";
import { handleApiError } from "../utils/handleApiError";
import ExpenseForm from "../components/ExpenseForm";
import ConfirmDeleteModal from "../components/ConfirmDeleteModal";
import DesktopExpenseList from "../components/DesktopExpenseList/DesktopExpenseList";
import MobileExpenseList from "../components/MobileExpenseList";
import LoadingErrorMessage from "../components/LoadingErrorMessage";

export default function ExpenseManagementPage() {
    const [expenseList, setExpenseList] = useState([]);
    const [categoryList, setCategoryList] = useState([]);
    const [formMode, setFormMode] = useState(null);
    const [selectedExpense, setSelectedExpense] = useState(null);
    const [processing, setProcessing] = useState(false);
    const [loadingExpenses, setLoadingExpenses] = useState(false);
    const [loadingCategories, setLoadingCategories] = useState(false);
    const [isLoadingError, setIsLoadingError] = useState(false);

    useEffect(() => {
        getExpenseList();
        getcategoryList();
    }, []);

    async function getExpenseList() {
        setLoadingExpenses(true);
        setIsLoadingError(false);
        try {
            const response = await apiClient.get("/expenses/");
            setExpenseList(response.data);

        } catch (error) {
            setIsLoadingError(true);
            handleApiError(error);
        } finally {
            setLoadingExpenses(false);
        }
    }

    async function getcategoryList() {
        setLoadingCategories(true);
        try {
            const response = await apiClient.get("/categories/");
            setCategoryList(response.data);
        } catch (error) {
            handleApiError(error);
        } finally {
            setLoadingCategories(false);
        }

    }

    async function addExpense(formData) {
        setProcessing(true);
        try {
            await apiClient.post("/expenses/", formData);
            closeForm()
            await getExpenseList();
        } catch (error) {
            handleApiError(error);
        } finally {
            setProcessing(false);
        }

    }
    async function updateExpense(formData, expenseId) {
        setProcessing(true);
        try {
            await apiClient.put(`/expenses/${expenseId}`, formData);
            closeForm()
            await getExpenseList();
        } catch (error) {
            handleApiError(error);
        } finally {
            setProcessing(false);
        }
    }
    async function deleteExpense(expenseId) {
        try {
            setProcessing(true);
            await apiClient.delete(`/expenses/${expenseId}`);
            closeForm()
            await getExpenseList();
        } catch (error) {
            handleApiError(error);
        } finally {
            setProcessing(false);
        }
    }
    function handleExpenseAddition() {
        setFormMode('ADD')
    }
    function handleExpenseUpdate(expense) {
        setSelectedExpense(expense)
        setFormMode('UPDATE')

    }
    function handleExpenseDelete(expense) {
        setSelectedExpense(expense)
        setFormMode('DELETE')
    }

    function closeForm() {
        setSelectedExpense(null);
        setFormMode(null)
    }

    return (
        <div className="min-vh-100 bg-dark text-white p-3 ">
            <div className="d-flex gap-3 flex-column flex-md-row justify-content-between  mb-3">
                <h1 className="mb-3">Expense Management</h1>
                {!formMode && <button className="btn btn-primary border border-info  fs-5 align-self-end align-self-md-center " aria-controls="example-collapse-text" aria-expanded={`${formMode !== null}`} onClick={handleExpenseAddition}>+ Add Expense</button>}
            </div>
            <div className={`collapse ${formMode ? "show" : ""} `} id="example-collapse-text" >
                {formMode === 'ADD' && <ExpenseForm key={'ADD'} handleSaveExpense={addExpense} mode={formMode} handleClose={closeForm} processing={processing} categoryList={categoryList} loadingCategories={loadingCategories} />}
                {formMode === 'UPDATE' && <ExpenseForm key={selectedExpense.id} handleSaveExpense={updateExpense} expense={selectedExpense} mode={formMode} handleClose={closeForm} processing={processing} categoryList={categoryList} loadingCategories={loadingCategories} />}
                {formMode === 'DELETE' && <ConfirmDeleteModal id={selectedExpense.id} handleCancel={closeForm} handleConfirmDelete={deleteExpense} deleting={processing} />}
            </div>
            <hr />
            <div>
                {loadingExpenses ? (
                    <h3>Loading expenses...</h3>
                ) : (isLoadingError) ? (
                    <LoadingErrorMessage onRetry={getExpenseList} resourceName={"expenses"} />
                ) : (expenseList.length === 0) ? (
                    <div className="text-center py-5">
                        <h4>No expenses yet</h4>
                        <p className="text-secondary">
                            Start tracking your expenses by adding one.
                        </p>
                    </div>
                ) : (
                    <>
                        <h3 className="mb-3">Your Expenses</h3>
                        <div className="d-none d-md-block">
                            <DesktopExpenseList expenseList={expenseList} handleExpenseUpdate={handleExpenseUpdate} handleExpenseDelete={handleExpenseDelete} />
                        </div>
                        <div className="d-block d-md-none">
                            <MobileExpenseList expenseList={expenseList} handleExpenseUpdate={handleExpenseUpdate} handleExpenseDelete={handleExpenseDelete} />
                        </div>
                    </>
                )
                }

            </div>
        </div>
    )
}