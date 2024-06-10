using System.ComponentModel.DataAnnotations;

namespace TheWebApiServer.Requests
{
    public class ResetPasswordRequest
    {
        [Required]
        [EmailAddress]
        public string Email { get; set; }
        [Required]
        public string Code { get; set; }
        [Required]
        public string NewPassword { get; set; }
    }
}
