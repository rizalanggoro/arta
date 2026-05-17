package auth

// RegisterReq represents user registration input.
type RegisterReq struct {
	Email    string `json:"email"`
	Name     string `json:"name"`
	Password string `json:"password"`
} // @name RegisterReq

// LoginReq represents user login input.
type LoginReq struct {
	Email    string `json:"email"`
	Password string `json:"password"`
} // @name LoginReq
