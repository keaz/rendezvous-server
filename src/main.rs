use async_std::task;
use rendezvous_server::server::accept_loop;
fn main() {
    log4rs::init_file("config/log4rs.yaml", Default::default()).unwrap();
    let _result = task::block_on(accept_loop("127.0.0.1:8080"));
}
