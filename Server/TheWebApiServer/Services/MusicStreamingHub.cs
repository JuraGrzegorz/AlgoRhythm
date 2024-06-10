using Microsoft.AspNetCore.SignalR;
using TheWebApiServer.IServices;

namespace TheWebApiServer.Services
{
    public sealed class MusicStreamingHub : Hub<IMusicStreamingHub>
    {
        private static int connectedClientsCount = 0;
        public override async Task OnConnectedAsync()
        {
            Interlocked.Increment(ref connectedClientsCount);
            var connectionId = Context.ConnectionId;
            await Clients.Client(connectionId).ReceiveMassage(connectionId);
            base.OnConnectedAsync();
        }
        
        public override async Task OnDisconnectedAsync(Exception exception)
        {
            Interlocked.Decrement(ref connectedClientsCount);
            base.OnDisconnectedAsync(exception);
        }

        public static int GetAllClients()
        {
            return connectedClientsCount;
        }



    }
}
