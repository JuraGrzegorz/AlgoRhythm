namespace TheWebApiServer.IServices
{
    public interface IMusicStreamingHub
    {
        Task ReceiveMassage(string ConnectionId);
        Task GetMusicBytes(byte[] buffer);
        Task GetMusicBytes(string v);
    }
}
