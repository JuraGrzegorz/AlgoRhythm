using Microsoft.AspNetCore.SignalR;
using TheWebApiServer.IServices;

namespace TheWebApiServer.Services
{
    public sealed class MusicStreamingHub : Hub<IMusicStreamingHub>
    {
        public override async Task OnConnectedAsync()
        {
            var connectionId = Context.ConnectionId;
            await Clients.Client(connectionId).ReceiveMassage(connectionId);
        }
    }
}
