import { useEffect, useState } from "react";

export default function ExpenseForm({ processing, handleSaveExpense, mode, handleClose, expense, categoryList, loadingCategories }) {

    const [formData, setFormData] = useState(() => getInitialFormData(expense));


    const [errors, setErrors] = useState({});


    function getInitialFormData() {
        if (mode === 'UPDATE' && expense) {
            return {
                amount: `${expense.amount}`,
                description: `${expense.description}`,
                expenseDate: `${expense.expenseDate}`,
                categoryId: `${expense.categoryId}`
            }

        }

        return {
            amount: "",
            description: "",
            expenseDate: `${new Date().toISOString().split('T')[0]}`,
            categoryId: null,
        }

    }

    function handleChange(e) {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });

        setErrors({
            ...errors,
            [e.target.name]: ""
        });
    }

    function handleSubmit(e) {
        e.preventDefault()
        const validationError = validateForm();

        if (Object.keys(validationError).length > 0) {
            setErrors(validationError);
            return;
        }
        if (mode === 'ADD') {
            handleSaveExpense(formData);
        }
        else if (mode === 'UPDATE') {
            handleSaveExpense(formData, expense.id);
        }
    }

    function validateForm() {
        const validationError = {};

        const amount = formData.amount.trim();
        if (amount.length === 0) {
            validationError.amount = "amount cannot be empty"
        }
        else if (!(/^\d+(\.\d{1,2})?$/).test(amount)) {
            validationError.amount = "amount must have number only";
        }

        else if (Number(amount) <= 0) {
            validationError.amount = "amount must be positive";
        }

        const description = formData.description.trim();
        if (description.length === 0) {
            validationError.description = "description cannot be empty"
        }
        else if (!(/[a-zA-Z]/).test(description)) {
            validationError.description = "description must have atleast one alphabet"
        }

        const expenseDate = formData.expenseDate;
        if (expenseDate && new Date(expenseDate) > new Date()) {
            validationError.expenseDate = "expense date cannot be in future";
        }

        const categoryId = formData.categoryId;
        if (!categoryId) {
            validationError.category = "select a category"
        }
        return validationError;
    }



    return (
        <div className="border border-white rounded-3 p-3">
            <div className="d-flex justify-content-between">
                <h3>{mode === 'ADD' ? 'Add' : "Update"} Expense</h3>
                <button type="button" className="btn-close bg-white  " onClick={handleClose} aria-label="Close" disabled={processing}></button>
            </div>

            <form  onSubmit={handleSubmit} disabled={processing}>
                <fieldset disabled={processing}>
                    <div className="mb-3 ">
                        <label htmlFor="amount" className="form-label">Amount</label>
                        <input type="text" name="amount" onChange={handleChange} value={formData.amount} className="form-control" id="amount" placeholder="0.00" />
                        {errors.amount && <div className="text-danger">{errors.amount}</div>}
                    </div>
                    <div className="mb-3 ">
                        <label htmlFor="description" className="form-label">Description</label>
                        <input type="text" name="description" onChange={handleChange} value={formData.description} className="form-control" id="description" placeholder="Enter description" />
                        {errors.description && <div className="text-danger">{errors.description}</div>}
                    </div>
                    <div className="row mb-3 gap-3">
                        <div className=" col col-12 col-md-4 ">
                            <label htmlFor="date" className="form-label">Expense Date</label>
                            <input type="date" name="expenseDate" onChange={handleChange} value={formData.expenseDate} className="form-control" id="date" placeholder="Select date" />
                            {errors.expenseDate && <div className="text-danger">{errors.expenseDate}</div>}
                        </div>
                        <div className="col col-12 col-md-4">
                            <label htmlFor="category" className="form-label">Category</label>
                            <select id="category" name="categoryId" value={formData.categoryId} onChange={handleChange} className="form-select" >
                                <option value="">{loadingCategories ? "loading..." : "select category"}</option>
                                {categoryList.map((category) => {
                                    return <option key={category.id} value={category.id} >{category.name}</option>
                                })}
                            </select>
                            {errors.category && <div className="text-danger">{errors.category}</div>}
                        </div>
                    </div>
                    <button type="submit" className="btn btn-success fs-6 px-4" disabled={processing}>
                        {(processing) ? "Saving" : "Save"}
                    </button>
                </fieldset>
            </form>
        </div>
    )
}