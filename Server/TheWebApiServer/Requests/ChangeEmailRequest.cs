using System.ComponentModel.DataAnnotations;

namespace TheWebApiServer.Requests
{
    public class ChangeEmailRequest
    {
        [Required]
        [EmailAddress]
        public string Email { get; set; }
    }
}
