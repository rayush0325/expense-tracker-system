export default function RegisterPage() {
    return (
        <div className="d-flex justify-content-center align-items-center vh-100 bg-dark text-white">
            <form className="w-50 p-3 border border-secondary rounded bg-secondary text-white">
                <div className="mb-3">
                    <label htmlFor="exampleFormControlInput1" className="form-label">Email address</label>
                    <input type="email" className="form-control" id="email" placeholder="name@example.com" />
                </div>
                <div className="mb-3">
                    <label htmlFor="exampleFormControlInput1" className="form-label">Full Name</label>
                    <input className="form-control" type="text" id="fullName" placeholder="full name" aria-label="default input example" />

                </div>
                <div className="mb-3">
                    <label htmlFor="inputPassword5" className="form-label">Password</label>
                    <input type="password" id="password" className="form-control"/>
                </div>
                <button type="submit" className="btn btn-primary">Submit</button>
            </form>
        </div>
    )
}