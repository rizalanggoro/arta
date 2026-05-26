package release

// CreateReleaseReq defines payload for creating a release.
type CreateReleaseReq struct {
	URL         string `json:"url"`
	VersionCode int    `json:"version_code"`
} // @name CreateReleaseReq
