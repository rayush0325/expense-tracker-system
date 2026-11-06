export function HomePage(){
    return (
        // <div className="min-vh-100 d-flex justify-content-center align-items-center bg-dark text-white">
        //     <div className="w-75 p-3 border border-white rounded bg-dark text-white text-center">
        //         <h3 >Welcome To</h3>
        //         <h1>Expense Tracker System</h1>
        //     </div>
        // </div>

         <div className="min-vh-100 d-flex justify-content-center align-items-center bg-dark text-white p-3">

            <div className="text-center w-100" style={{ maxWidth: "750px" }}>

                <p className="text-secondary fs-5 mb-2">
                    Welcome to
                </p>

                <h1 className="fw-bold display-4 mb-3">
                    Expense Tracker System
                </h1>

                <p className="text-secondary fs-6">
                    Manage your expenses. Track your spending. Stay in control.
                </p>

                <hr className="border-secondary w-50 mx-auto mt-4" />

            </div>

        </div>
    )
}