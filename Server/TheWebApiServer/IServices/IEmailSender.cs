namespace TheWebApiServer.IServices
{
    public interface IEmailSender
    {
        Task SendEmailAsync(string email, string subject, string htmlMessage);
        Task SendPasswordResetCodeAsync(string email, string code);
    }
}
