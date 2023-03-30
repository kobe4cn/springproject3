const UserProfile=({name,age,gender,imagenumber,...props})=>{
    gender=gender=="MALE"?"men":"women";
    return (
        <div>
            <h1>{name}</h1>
            <p>{age}</p>
           <img src={`https://randomuser.me/api/portraits/${gender}/${imagenumber}.jpg`}/>
            {props.children}
        </div>

    );
}

export default UserProfile;
